package edu.nipun.taskmanager.config;

import edu.nipun.taskmanager.entity.User;
import edu.nipun.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    @Override
    public void run(String... args) {
        String username = adminProperties.getUsername();
        String password = adminProperties.getPassword();

        if (username != null && password != null) {
            if (!userRepository.existsByUsername(username)) {
                User adminUser = new User();
                adminUser.setUsername(username);
                adminUser.setPassword(passwordEncoder.encode(password));
                userRepository.save(adminUser);

                log.info("Default admin user created with username: {}", username);
            } else {
                log.info("Admin user already exists, skipping initialization");
            }
        } else {
            log.warn("Admin credentials not configured, skipping default user creation");
        }
    }
}