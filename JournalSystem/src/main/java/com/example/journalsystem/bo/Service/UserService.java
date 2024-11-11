package com.example.journalsystem.bo.Service;

import com.example.journalsystem.bo.model.Role;
import com.example.journalsystem.bo.model.User;
import com.example.journalsystem.db.MessageRepository;
import com.example.journalsystem.db.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create a new user, encoding the password before saving.
     */
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode password
        return userRepository.save(user);
    }

    /**
     * Find a user by username.
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Assign a role to a user.
     */
    public User assignRoleToUser(Long userId, Set<Role> role) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRoles(role);
            return userRepository.save(user);
        } else {
            throw new EntityNotFoundException("User not found");
        }
    }

    /**
     * Find users by their role type.
     */
    public List<User> findByRoleType(Role.RoleType roleType) {
        return userRepository.findByRoles_Name(roleType);
    }

    /**
     * Authenticate a user by username and password.
     */
    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
