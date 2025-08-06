package com.project.hotel.service;

import com.project.hotel.entity.Customer;
import com.project.hotel.entity.Staff;
import com.project.hotel.entity.User;
import com.project.hotel.repository.CustomerRepository;
import com.project.hotel.repository.ManagerRepository;
import com.project.hotel.repository.StaffRepository;
import com.project.hotel.repository.UserRepository;
import com.project.hotel.security.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EncryptionUtil encryptionUtil;
    @Autowired
    private UserImageService userImageService;

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public Customer findCustomerByUserId(Long id) {
        return customerRepository.findByUserId(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Staff findStaffByUserId(Long userId) {
        return staffRepository.findByUserId(userId);
    }

    public void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    public void saveStaff(Staff staff) {
        staffRepository.save(staff);
    }

    public Staff findStaffById(Long id) {
        return staffRepository.findById(id).orElseThrow();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void saveUserWithCustomer(String name, String username, String email, String phone, String password, Integer age,
                                     String gender, String role, String nrc, String address) throws Exception {

        if (username == null || !username.equals(username.toLowerCase())) {
            throw new IllegalArgumentException("Username must be lowercase");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (!username.matches("[a-z0-9]+")) {
            throw new IllegalArgumentException("Username contains invalid characters");
        }

        String encryptedPassword = encryptionUtil.encrypt(password);

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(encryptedPassword);
        user.setAge(age);
        user.setGender(gender);
        user.setRole(role);

        User savedUser = userRepository.save(user);
        if (user.getRole().equals("customer")) {
            Customer customer = new Customer();
            customer.setUser(savedUser);
            customer.setNrc(nrc);
            customer.setAddress(address);
            customerRepository.save(customer);
        }
    }

    public void updateUserProfile(String username, MultipartFile imageFile) {
        User user = findUserByUsername(username);
        if (user != null) {

            if (imageFile != null && !imageFile.isEmpty()) {

                try {
                    userImageService.deleteImage(user.getImagePath());
                    String imagePath = userImageService.saveImage(imageFile, user.getUsername());
                    if (imagePath != null) {
                        user.setImagePath(imagePath);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            saveUser(user);
        }
    }

    public void deleteUserProfile(String username, String imagePath) {

        User user = findUserByUsername(username);
        if (user != null) {
            user.setImagePath(null);
            try {
                userImageService.deleteImage(imagePath);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
