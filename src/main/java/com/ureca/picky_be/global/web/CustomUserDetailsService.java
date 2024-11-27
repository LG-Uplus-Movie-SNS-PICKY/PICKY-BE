package com.ureca.picky_be.global.web;

import com.ureca.picky_be.base.persistence.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long userId = Long.parseLong(username);

        return userRepository.findById(userId)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getId().toString(),
                        user.getRole().toString(),
                        List.of(new SimpleGrantedAuthority(user.getRole().name()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
