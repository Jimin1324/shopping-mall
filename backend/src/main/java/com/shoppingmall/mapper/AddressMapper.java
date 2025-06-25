package com.shoppingmall.mapper;

import com.shoppingmall.model.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {
    
    List<Address> findByUserId(@Param("userId") Long userId);
    
    Address findById(@Param("id") Long id);
    
    Address findDefaultByUserId(@Param("userId") Long userId);
    
    void insert(Address address);
    
    void update(Address address);
    
    void delete(@Param("id") Long id);
    
    void removeDefaultForUser(@Param("userId") Long userId);
}