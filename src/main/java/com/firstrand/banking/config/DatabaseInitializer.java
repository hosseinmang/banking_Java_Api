package com.firstrand.banking.config;

import com.firstrand.banking.model.ERole;
import com.firstrand.banking.model.Role;
import com.firstrand.banking.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        initRoles();
    }
    
    private void initRoles() {
        // Check if roles already exist
        if (roleRepository.count() == 0) {
            // Create roles
            Role userRole = new Role(ERole.ROLE_USER);
            Role adminRole = new Role(ERole.ROLE_ADMIN);
            
            // Save roles
            roleRepository.save(userRole);
            roleRepository.save(adminRole);
        }
    }
}
