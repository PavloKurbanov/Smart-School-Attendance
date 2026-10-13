package org.example.smart_school_attendance.security.service;

import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.repository.UserRepository;
import org.example.smart_school_attendance.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class UsersDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UsersDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(()
                -> new UsernameNotFoundException("User not found with email: " + username));
        return new SecurityUser(user);
    }
}
