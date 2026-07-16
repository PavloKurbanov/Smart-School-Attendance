package org.example.smart_school_attendance.sequrity.service;

import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.repository.UserRepository;
import org.example.smart_school_attendance.sequrity.SecurityUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UsersDetailsService implements UsersDetailsService {
    public final UserRepository userRepository;

    public UsersDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return new SecurityUser(user);
    }
}
