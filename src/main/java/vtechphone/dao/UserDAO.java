package vtechphone.dao;

import java.util.List;
import vtechphone.entity.User;

public interface UserDAO {
    User create(User entity);
    void update(User entity);
    void delete(User entity);
    List<User> findAll();
    User findById(Integer id);
    User findByEmail(String email);
    User findByPhone(String phone);
    User findByEmailOrPhone(String username);
    // 🔥 Đã xóa: int nextUserId();
}