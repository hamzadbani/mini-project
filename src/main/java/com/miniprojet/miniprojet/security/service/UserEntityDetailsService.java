package com.miniprojet.miniprojet.security.service;

import com.miniprojet.miniprojet.entity.User;
import com.miniprojet.miniprojet.repository.UserRepository;
import com.miniprojet.miniprojet.security.util.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserEntityDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserEntityDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));

        return new CustomUserDetails(user.getUsername(), user.getPassword(), user.getEmail());
    }
}
