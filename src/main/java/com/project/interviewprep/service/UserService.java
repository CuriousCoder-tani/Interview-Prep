package com.project.interviewprep.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.project.interviewprep.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.project.interviewprep.model.User;


public class UserService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HttpServletRequest request;

	@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    
    if (user == null) {
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    System.out.println("User found: " + user.getUsername());
    System.out.println("User role: " + user.getRole());

    user.setLoginAt(LocalDateTime.now());
    userRepository.save(user);

    HttpSession session = request.getSession();
    session.setAttribute("user", user);

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(user.getRole()));

    return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            authorities
    );
}

}
