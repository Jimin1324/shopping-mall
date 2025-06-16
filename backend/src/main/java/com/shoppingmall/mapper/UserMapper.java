package com.shoppingmall.mapper;

import com.shoppingmall.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    
    // Create
    int insert(User user);
    
    // Read
    Optional<User> findById(@Param("id") Long id);
    Optional<User> findByEmail(@Param("email") String email);
    List<User> findAll();
    List<User> findAllActive();
    
    // Update
    int update(User user);
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    int deactivate(@Param("id") Long id);
    int activate(@Param("id") Long id);
    
    // Delete
    int deleteById(@Param("id") Long id);
    
    // Check existence
    boolean existsByEmail(@Param("email") String email);
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
}