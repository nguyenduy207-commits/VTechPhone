package vtechphone.service;

import vtechphone.entity.User;
import java.time.LocalDate;
import java.util.List;

public interface UserService {
    User loginByEmail(String email, String password);
    User register(String fullName, String email, String phone, String address, String password);
    User registerUser(String fullName, String email, String phone, String address, String password, 
    		int roleId, String staffPosition, LocalDate hireDate);
    
    boolean existsByEmail(String email);
    User findByEmail(String email);
    void update(User user);
    void deleteUser(Integer id);
    List<User> findAllUsers();
}