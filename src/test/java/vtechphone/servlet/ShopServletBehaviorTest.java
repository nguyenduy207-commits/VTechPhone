package vtechphone.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vtechphone.entity.CartItem;
import vtechphone.entity.Order;
import vtechphone.entity.OrderItem;
import vtechphone.entity.Product;
import vtechphone.entity.User;
import vtechphone.service.CartService;
import vtechphone.service.OrderService;
import vtechphone.service.ProductService;

/**
 * TEST HÀNH VI NGHIỆP VỤ (Given - When - Then) cho ShopServlet.
 * Mỗi test là một KỊCH BẢN của khách hàng: thêm giỏ, sửa số lượng, thanh toán.
 * Service/DAO bị mock nên không cần database.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Hành vi mua hàng (ShopServlet)")
class ShopServletBehaviorTest {

    @Mock HttpServletRequest req;
    @Mock HttpServletResponse resp;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;

    @Mock ProductService productService;
    @Mock CartService cartService;
    @Mock OrderService orderService;

    ShopServlet servlet;
    User customer;

    @BeforeEach
    void setUp() {
        servlet = new ShopServlet();
        servlet.productService = productService; // field package-private => gán mock
        servlet.cartService = cartService;
        servlet.orderService = orderService;
        customer = User.builder().userId(7).roleId(3).fullName("Khách A").build();

        lenient().when(req.getContextPath()).thenReturn("/VTechPhone");
    }

    // ---------- helpers ----------
    /** Giả lập khách đã đăng nhập: AuthUtil đọc user từ session. */
    private void givenLoggedInCustomer() {
        lenient().when(req.getSession(false)).thenReturn(session);
        lenient().when(req.getSession()).thenReturn(session);
        lenient().when(session.getAttribute("user")).thenReturn(customer);
    }

    private Product product(int id, String name, String price, int stock) {
        Product p = new Product();
        p.setProductId(id);
        p.setName(name);
        p.setPrice(new BigDecimal(price));
        p.setStock(stock);
        return p;
    }

    private CartItem cartItem(Product p, int qty) {
        CartItem c = new CartItem();
        c.setUser(customer);
        c.setProduct(p);
        c.setQuantity(qty);
        return c;
    }

    // =====================================================================
    @Nested
    @DisplayName("Thêm sản phẩm vào giỏ (POST /shop/cart)")
    class AddToCart {

        @Test
        @DisplayName("Chưa đăng nhập => chuyển về /login, không đụng tới giỏ hàng")
        void notLoggedIn_redirectsToLogin() throws Exception {
            when(req.getPathInfo()).thenReturn("/cart");   // getSession(false) mặc định trả null

            servlet.doPost(req, resp);

            verify(resp).sendRedirect("/VTechPhone/login");
            verifyNoInteractions(cartService);
        }

        @Test
        @DisplayName("Sản phẩm hết hàng (stock = 0) => báo lỗi, KHÔNG thêm vào giỏ")
        void soldOut_showsErrorAndDoesNotAdd() throws Exception {
            givenLoggedInCustomer();
            when(req.getPathInfo()).thenReturn("/cart");
            when(req.getParameter("productId")).thenReturn("5");
            when(productService.getProductById(5)).thenReturn(product(5, "iPhone 15", "20000000", 0));

            servlet.doPost(req, resp);

            verify(session).setAttribute("cartError", "Sản phẩm \"iPhone 15\" đã bán hết!");
            verify(resp).sendRedirect("/VTechPhone/shop/products");
            verify(cartService, never()).addToCart(anyInt(), anyInt(), anyInt());
        }

        @Test
        @Tag("smoke")
        @DisplayName("Còn hàng => thêm đúng 1 sản phẩm vào giỏ của user rồi sang trang giỏ")
        void inStock_addsOneItem() throws Exception {
            givenLoggedInCustomer();
            when(req.getPathInfo()).thenReturn("/cart");
            when(req.getParameter("productId")).thenReturn("5");
            when(productService.getProductById(5)).thenReturn(product(5, "iPhone 15", "20000000", 10));

            servlet.doPost(req, resp);

            verify(cartService).addToCart(7, 5, 1);
            verify(resp).sendRedirect("/VTechPhone/shop/cart");
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("Sửa / xóa trong giỏ (GET /shop/cart?action=...)")
    class UpdateCart {

        private void givenUpdateRequest(String quantity, Product p) {
            givenLoggedInCustomer();
            when(req.getPathInfo()).thenReturn("/cart");
            when(req.getParameter("action")).thenReturn("update");
            when(req.getParameter("productId")).thenReturn("5");
            when(req.getParameter("quantity")).thenReturn(quantity);
            when(productService.getProductById(5)).thenReturn(p);
        }

        @Test
        @DisplayName("Số lượng hợp lệ (trong tồn kho) => cập nhật đúng số lượng")
        void validQuantity_updates() throws Exception {
            givenUpdateRequest("2", product(5, "iPhone 15", "20000000", 10));

            servlet.doGet(req, resp);

            verify(cartService).updateQuantity(7, 5, 2);
            verify(resp).sendRedirect("/VTechPhone/shop/cart");
        }

        @Test
        @DisplayName("Số lượng = tồn kho (biên trên) => chấp nhận, không cảnh báo")
        void quantityEqualsStock_isAccepted() throws Exception {
            givenUpdateRequest("10", product(5, "iPhone 15", "20000000", 10));

            servlet.doGet(req, resp);

            verify(cartService).updateQuantity(7, 5, 10);
            verify(session, never()).setAttribute(eq("cartError"), any());
        }

        @Test
        @DisplayName("Số lượng vượt tồn kho => tự hạ về mức tồn kho và báo cho khách")
        void quantityOverStock_isClampedToStock() throws Exception {
            givenUpdateRequest("10", product(5, "iPhone 15", "20000000", 3));

            servlet.doGet(req, resp);

            verify(cartService).updateQuantity(7, 5, 3);
            verify(session).setAttribute("cartError",
                    "Sản phẩm \"iPhone 15\" chỉ còn 3 sản phẩm. Đã tự động điều chỉnh số lượng.");
        }

        @ParameterizedTest(name = "số lượng nhập \"{0}\" => xóa khỏi giỏ")
        @ValueSource(strings = {"0", "-1", "abc", "", " "})
        @DisplayName("Số lượng <= 0, rỗng hoặc không phải số => xóa sản phẩm khỏi giỏ")
        void nonPositiveOrInvalidQuantity_removesItem(String qty) throws Exception {
            givenUpdateRequest(qty, product(5, "iPhone 15", "20000000", 10));

            servlet.doGet(req, resp);

            verify(cartService).removeFromCart(7, 5);
            verify(cartService, never()).updateQuantity(anyInt(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("action=remove => xóa đúng sản phẩm của đúng user")
        void removeAction_removesItem() throws Exception {
            givenLoggedInCustomer();
            when(req.getPathInfo()).thenReturn("/cart");
            when(req.getParameter("action")).thenReturn("remove");
            when(req.getParameter("productId")).thenReturn("5");

            servlet.doGet(req, resp);

            verify(cartService).removeFromCart(7, 5);
            verify(resp).sendRedirect("/VTechPhone/shop/cart");
        }

        @Test
        @DisplayName("Xem giỏ khi chưa đăng nhập => chuyển về /login")
        void viewCart_notLoggedIn_redirectsToLogin() throws Exception {
            when(req.getPathInfo()).thenReturn("/cart");

            servlet.doGet(req, resp);

            verify(resp).sendRedirect("/VTechPhone/login");
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("Thanh toán (POST /shop/checkout)")
    class Checkout {

        private void givenCheckoutForm(String... selectedProductIds) {
            givenLoggedInCustomer();
            when(req.getPathInfo()).thenReturn("/checkout");
            when(req.getParameterValues("selectedProductIds"))
                    .thenReturn(selectedProductIds.length == 0 ? null : selectedProductIds);
            lenient().when(req.getParameter("receiverName")).thenReturn("Nguyễn Văn A");
            lenient().when(req.getParameter("receiverPhone")).thenReturn("0912345678");
            lenient().when(req.getParameter("shippingAddress")).thenReturn("12 Lê Lợi, Q1, TP.HCM");
        }

        @Test
        @DisplayName("Không chọn sản phẩm nào => báo lỗi, không tạo đơn")
        void nothingSelected_showsError() throws Exception {
            givenCheckoutForm();   // không có id nào

            servlet.doPost(req, resp);

            verify(session).setAttribute("selectionError", "Vui lòng chọn ít nhất một sản phẩm để thanh toán.");
            verify(resp).sendRedirect("/VTechPhone/shop/cart");
            verifyNoInteractions(orderService);
        }

        @Test
        @DisplayName("Id được chọn không có trong giỏ => báo lỗi, không tạo đơn")
        void selectedIdNotInCart_showsError() throws Exception {
            givenCheckoutForm("99");
            Product p = product(1, "iPhone 15", "20000000", 10);
            when(cartService.getCartItemsByUserId(7)).thenReturn(List.of(cartItem(p, 1)));

            servlet.doPost(req, resp);

            verify(session).setAttribute("selectionError", "Không tìm thấy sản phẩm nào được chọn.");
            verifyNoInteractions(orderService);
        }

        @Test
        @Tag("smoke")
        @DisplayName("Thanh toán thành công: đơn PENDING, tổng = tiền hàng + 1% phí ship, dọn giỏ")
        void successfulCheckout_createsPendingOrderWithShippingFee() throws Exception {
            Product phone = product(1, "iPhone 15", "10000000", 10);
            Product case_ = product(2, "Ốp lưng", "5000000", 10);
            givenCheckoutForm("1", "2");
            when(cartService.getCartItemsByUserId(7))
                    .thenReturn(List.of(cartItem(phone, 2), cartItem(case_, 1)));

            servlet.doPost(req, resp);

            ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
            verify(orderService).createOrder(captor.capture());
            Order order = captor.getValue();

            // hàng: 2 x 10.000.000 + 1 x 5.000.000 = 25.000.000 ; ship 1% = 250.000
            assertEquals(0, new BigDecimal("25250000").compareTo(order.getTotalAmount()));
            assertEquals("PENDING", order.getStatus());
            assertSame(customer, order.getCustomer());
            assertEquals("Nguyễn Văn A", order.getReceiverName());
            assertEquals("0912345678", order.getReceiverPhone());
            assertEquals("12 Lê Lợi, Q1, TP.HCM", order.getShippingAddress());
            assertEquals(2, order.getItems().size());

            OrderItem first = order.getItems().get(0);
            assertEquals(2, first.getQuantity());
            assertEquals(0, new BigDecimal("10000000").compareTo(first.getUnitPrice()));

            // Dọn giỏ: các sản phẩm đã mua bị xóa khỏi giỏ
            verify(cartService).removeFromCart(7, 1);
            verify(cartService).removeFromCart(7, 2);
            verify(session).setAttribute("message", "Đặt hàng thành công! Vui lòng chờ xử lý.");
            verify(resp).sendRedirect("/VTechPhone/shop/my-orders");
        }

        @Test
        @DisplayName("Chỉ mua sản phẩm được tick; sản phẩm không tick vẫn nằm trong giỏ")
        void onlySelectedItemsAreOrdered() throws Exception {
            Product a = product(1, "iPhone 15", "10000000", 10);
            Product b = product(2, "Ốp lưng", "5000000", 10);
            givenCheckoutForm("1");
            when(cartService.getCartItemsByUserId(7)).thenReturn(List.of(cartItem(a, 1), cartItem(b, 1)));

            servlet.doPost(req, resp);

            ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
            verify(orderService).createOrder(captor.capture());
            assertEquals(1, captor.getValue().getItems().size());
            verify(cartService).removeFromCart(7, 1);
            verify(cartService, never()).removeFromCart(7, 2);
        }

        @ParameterizedTest(name = "tiền hàng {0} => tổng thanh toán {1}")
        @CsvSource({
            "50,      51",         // ship 0.5  -> làm tròn HALF_UP = 1
            "149,     150",        // ship 1.49 -> 1
            "150,     152",        // ship 1.5  -> 2
            "1000000, 1010000"     // ship 10.000
        })
        @DisplayName("Phí ship = 1% tiền hàng, làm tròn HALF_UP về số nguyên")
        void shippingFeeIsOnePercentRoundedHalfUp(String goods, String expectedTotal) throws Exception {
            Product p = product(1, "Phụ kiện", goods, 10);
            givenCheckoutForm("1");
            when(cartService.getCartItemsByUserId(7)).thenReturn(List.of(cartItem(p, 1)));

            servlet.doPost(req, resp);

            ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
            verify(orderService).createOrder(captor.capture());
            assertEquals(0, new BigDecimal(expectedTotal).compareTo(captor.getValue().getTotalAmount()));
        }

        @Test
        @DisplayName("Lưu đơn thất bại (vd. hết hàng ở DB) => báo lỗi, GIỮ NGUYÊN giỏ hàng")
        void orderCreationFails_cartIsKept() throws Exception {
            Product p = product(1, "iPhone 15", "10000000", 10);
            givenCheckoutForm("1");
            when(cartService.getCartItemsByUserId(7)).thenReturn(List.of(cartItem(p, 1)));
            doThrow(new IllegalStateException("Sản phẩm iPhone 15 không đủ hàng!"))
                    .when(orderService).createOrder(any(Order.class));
            when(req.getRequestDispatcher("/WEB-INF/shop/cart.jsp")).thenReturn(dispatcher);

            servlet.doPost(req, resp);

            verify(req).setAttribute("error", "Lỗi thanh toán: Sản phẩm iPhone 15 không đủ hàng!");
            verify(dispatcher).forward(req, resp);
            verify(cartService, never()).removeFromCart(anyInt(), anyInt());
        }

        @Test
        @DisplayName("Chưa đăng nhập => không cho thanh toán")
        void notLoggedIn_redirectsToLogin() throws Exception {
            when(req.getPathInfo()).thenReturn("/checkout");

            servlet.doPost(req, resp);

            verify(resp).sendRedirect("/VTechPhone/login");
            verifyNoInteractions(orderService);
        }
    }
}
