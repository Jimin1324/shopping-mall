package com.shoppingmall.security;

import com.shoppingmall.mapper.UserMapper;
import com.shoppingmall.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Autowired
    public UserDetailsServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOpt = userMapper.findByEmail(email);
        
        if (!userOpt.isPresent()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return UserPrincipal.create(userOpt.get());
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        Optional<User> userOpt = userMapper.findById(id);
        
        if (!userOpt.isPresent()) {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }

        return UserPrincipal.create(userOpt.get());
    }
}