package src.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import src.application.model.User;
import src.application.repository.CustomerRepository;

import java.util.List;

// Updates uncrypted password into BCrypt and sets a standard password
@Service
public class UpdateToBCrypt {

    @Autowired
    private CustomerRepository customerRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void updatePasswordsToBCrypt() {
        List<User> users = customerRepository.findAll();

        for (User user : users) {
            String currentPassword = user.getPassword();
            if(!isBCrypt(currentPassword)){
                String newPassword = user.getFirstName() + "Pass";
                user.setPassword(passwordEncoder.encode(newPassword));
                customerRepository.save(user);
            }
        }
    }

    private boolean isBCrypt(String password) {
        return password != null && password.matches("^\\$2[aby]?\\$.{56}$");
    }

}
