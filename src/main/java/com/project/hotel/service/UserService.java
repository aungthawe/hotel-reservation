package com.project.hotel.service;

import com.project.hotel.entity.Customer;
import com.project.hotel.entity.Staff;
import com.project.hotel.entity.User;
import com.project.hotel.repository.CustomerRepository;
import com.project.hotel.repository.ManagerRepository;
import com.project.hotel.repository.StaffRepository;
import com.project.hotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }
    public Customer findCustomerByUserId(Long id){
        return customerRepository.findByUserId(id);
    }
    public List<User> findAllUsers(){
        return userRepository.findAll();
    }
    public  Staff findStaffByUserId(Long userId){
        return staffRepository.findByUserId(userId);
    }
    public Customer saveCustomer(Customer customer){
        return customerRepository.save(customer);
    }
    public Staff saveStaff(Staff staff){
        return staffRepository.save(staff);
    }
    public Staff findStaffById(Long id){
        return staffRepository.findById(id).orElseThrow();
    }
    public void saveUser(User user){
        userRepository.save(user);
    }
    public  User findUserById(Long id){
        return userRepository.findById(id).orElseThrow();
    }
    @Transactional
    public void saveUserWithCustomer(String name,String username,String email,String phone,String password,Integer age,
                                     String gender,String role,String nrc,String address){
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);
        user.setAge(age);
        user.setGender(gender);
        user.setRole(role);

        User savedUser = userRepository.save(user);
        if(user.getRole().equals("customer")){
            Customer customer = new Customer();
            customer.setUser(savedUser);
            customer.setNrc(nrc);
            customer.setAddress(address);
            customerRepository.save(customer);
        }
    }
}
