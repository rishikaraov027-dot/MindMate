package com.mindmate.service;

import com.mindmate.dto.UserRegisterForm;
import com.mindmate.entity.User;
import com.mindmate.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    public static final String SESSION_USER_KEY = "CURRENT_USER";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean register(UserRegisterForm form) {
        if (userRepository.existsByUsername(form.getUsername())) {
            return false;
        }

        User user = new User(
                form.getUsername().trim(),
                form.getDisplayName().trim(),
                form.getEmail() != null ? form.getEmail().trim() : null,
                passwordEncoder.encode(form.getPassword())
        );
        userRepository.save(user);
        return true;
    }

    public Optional<User> authenticate(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public void login(HttpSession session, User user) {
        session.setAttribute(SESSION_USER_KEY, user);
    }

    public void logout(HttpSession session) {
        session.removeAttribute(SESSION_USER_KEY);
    }

    public User getCurrentUser(HttpSession session) {
        Object u = session.getAttribute(SESSION_USER_KEY);
        return (u instanceof User) ? (User) u : null;
    }
}
