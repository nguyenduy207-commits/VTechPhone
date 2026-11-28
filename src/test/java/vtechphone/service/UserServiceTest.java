package vtechphone.service;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import vtechphone.dao.UserDAO;
import vtechphone.entity.User;
import vtechphone.util.PasswordUtil;

public class UserServiceTest {
    private UserServiceImpl service;
    private UserDAO dao;

    @BeforeMethod
    public void setUp() throws Exception {
        service = new UserServiceImpl();
        dao = Mockito.mock(UserDAO.class);
        Field field = UserServiceImpl.class.getDeclaredField("dao");
        field.setAccessible(true);
        field.set(service, dao);
    }

    @Test
    public void loginShouldReturnUserWhenCredentialsAreCorrect() {
        String hash = PasswordUtil.hashPassword("123456");
        User user = User.builder().email("test@gmail.com").passwordHash(hash).build();
        when(dao.findByEmail("test@gmail.com")).thenReturn(user);

        User result = service.loginByEmail("test@gmail.com", "123456");

        Assert.assertSame(result, user);
    }

    @Test
    public void loginShouldReturnNullWhenEmailIsBlank() {
        Assert.assertNull(service.loginByEmail(" ", "123456"));
        verifyNoInteractions(dao);
    }

    @Test
    public void loginShouldReturnNullWhenPasswordIsBlank() {
        Assert.assertNull(service.loginByEmail("test@gmail.com", " "));
        verifyNoInteractions(dao);
    }

    @Test
    public void loginShouldReturnNullWhenUserDoesNotExist() {
        when(dao.findByEmail("missing@gmail.com")).thenReturn(null);
        Assert.assertNull(service.loginByEmail("missing@gmail.com", "123456"));
    }

    @Test
    public void loginShouldReturnNullWhenPasswordIsWrong() {
        User user = User.builder().email("test@gmail.com")
                .passwordHash(PasswordUtil.hashPassword("123456")).build();
        when(dao.findByEmail("test@gmail.com")).thenReturn(user);

        Assert.assertNull(service.loginByEmail("test@gmail.com", "wrong123"));
    }

    @Test
    public void registerShouldCreateCustomerWithNormalizedEmail() {
        when(dao.findByEmail("new@gmail.com")).thenReturn(null);
        when(dao.create(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = service.register(" Nguyen Van A ", " NEW@gmail.com ",
                "0912345678", " HCM ", "123456");

        Assert.assertEquals(result.getFullName(), "Nguyen Van A");
        Assert.assertEquals(result.getEmail(), "new@gmail.com");
        Assert.assertEquals(result.getPhone(), "0912345678");
        Assert.assertEquals(result.getAddress(), "HCM");
        Assert.assertEquals(result.getRoleId(), 3);
        Assert.assertFalse(result.isActive());
        Assert.assertTrue(PasswordUtil.checkPassword("123456", result.getPasswordHash()));
        verify(dao).create(any(User.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void registerShouldRejectShortName() {
        service.register("A", "a@gmail.com", null, null, "123456");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void registerShouldRejectNonGmailAddress() {
        service.register("Nguyen Van A", "a@yahoo.com", null, null, "123456");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void registerShouldRejectInvalidPhone() {
        service.register("Nguyen Van A", "a@gmail.com", "12345", null, "123456");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void registerShouldRejectShortPassword() {
        service.register("Nguyen Van A", "a@gmail.com", null, null, "123");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void registerShouldRejectExistingEmail() {
        when(dao.findByEmail("a@gmail.com")).thenReturn(new User());
        service.register("Nguyen Van A", "a@gmail.com", null, null, "123456");
    }

    @Test
    public void existsByEmailShouldReturnTrueWhenFound() {
        when(dao.findByEmail("a@gmail.com")).thenReturn(new User());
        Assert.assertTrue(service.existsByEmail("a@gmail.com"));
    }

    @Test
    public void findByEmailShouldDelegateToDao() {
        User expected = new User();
        when(dao.findByEmail("a@gmail.com")).thenReturn(expected);
        Assert.assertSame(service.findByEmail("a@gmail.com"), expected);
    }

    @Test
    public void findAllUsersShouldReturnDaoResults() {
        when(dao.findAll()).thenReturn(java.util.List.of(new User(), new User()));
        Assert.assertEquals(service.findAllUsers().size(), 2);
    }

    @Test
    public void updateShouldCallDao() {
        User user = new User();
        service.update(user);
        verify(dao).update(user);
    }

    @Test
    public void deleteShouldDeleteExistingUser() {
        User user = new User();
        when(dao.findById(10)).thenReturn(user);
        service.deleteUser(10);
        verify(dao).delete(user);
    }

    @Test
    public void deleteShouldDoNothingWhenUserDoesNotExist() {
        when(dao.findById(10)).thenReturn(null);
        service.deleteUser(10);
        verify(dao, never()).delete(any());
    }
}
