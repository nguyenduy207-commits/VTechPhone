package vtechphone.service;

import vtechphone.dao.UserDAO;
import vtechphone.dao.UserDAOImpl;
import vtechphone.entity.User;
import vtechphone.util.PasswordUtil;
import java.time.LocalDate;
import java.util.List;

public class UserServiceImpl implements UserService {
	UserDAO dao = new UserDAOImpl();
	static final int ROLE_CUSTOMER = 3;

	@Override
	public User loginByEmail(String email, String password) {
		if (email == null || email.isBlank() || password == null || password.isBlank())
			return null;
		User user = dao.findByEmail(email.trim());
		if (user == null)
			return null;
		if (!PasswordUtil.checkPassword(password, user.getPasswordHash()))
			return null;
		return user;
	}

	@Override
	public User register(String fullName, String email, String phone, String address, String password) {
		return registerUser(fullName, email, phone, address, password, ROLE_CUSTOMER, null, null);
	}

	@Override
	public User registerUser(String fullName, String email, String phone, String address, String password, int roleId,
			String staffPosition, LocalDate hireDate) {
		fullName = (fullName != null) ? fullName.trim() : "";
		email = (email != null) ? email.trim().toLowerCase() : ""; // toLowerCase để đồng bộ
		phone = (phone != null && !phone.isBlank()) ? phone.trim() : null;
		address = (address != null) ? address.trim() : null;
		
		if (fullName == null || fullName.trim().length() < 2)
			throw new IllegalArgumentException("Họ tên không hợp lệ");
		if (email == null || !email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$"))
			throw new IllegalArgumentException("Email không hợp lệ");
		if (phone != null && !phone.isBlank() && !phone.matches("^0[0-9]{8,14}$"))
			throw new IllegalArgumentException("SĐT không hợp lệ");
		if (password == null || password.length() < 6)
			throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
		if (dao.findByEmail(email) != null)
			throw new IllegalArgumentException("Email đã được đăng ký");

		String hashed = PasswordUtil.hashPassword(password);
		if (hireDate == null)
			hireDate = LocalDate.now();

		User user = User.builder().roleId(roleId).fullName(fullName.trim()).email(email.trim()).passwordHash(hashed)
				.phone(phone != null ? phone.trim() : null).address(address != null ? address.trim() : null)
				.staffPosition(staffPosition).hireDate(hireDate).active(false) // 🔥 QUAN TRỌNG: Mặc định tài khoản chưa
																				// kích hoạt
				.customerPoint(0).customerRank("MEMBER").build();
		return dao.create(user);
	}

	@Override
	public boolean existsByEmail(String email) {
		return dao.findByEmail(email) != null;
	}

	@Override
	public User findByEmail(String email) {
		return dao.findByEmail(email);
	}

	@Override
	public void update(User user) {
		dao.update(user);
	}

	@Override
	public void deleteUser(Integer id) {
		User user = dao.findById(id);
		if (user != null)
			dao.delete(user);
	}

	@Override
	public List<User> findAllUsers() {
		return dao.findAll();
	}
}