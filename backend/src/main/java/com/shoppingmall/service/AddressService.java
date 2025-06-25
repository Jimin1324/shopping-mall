package com.shoppingmall.service;

import com.shoppingmall.exception.ResourceNotFoundException;
import com.shoppingmall.mapper.AddressMapper;
import com.shoppingmall.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressService {
    
    private final AddressMapper addressMapper;
    
    @Autowired
    public AddressService(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }
    
    public List<Address> getUserAddresses(Long userId) {
        return addressMapper.findByUserId(userId);
    }
    
    public Address createAddress(Long userId, String addressLine1, String addressLine2,
                                String city, String state, String zipCode, String country,
                                boolean isDefault) {
        Address address = new Address(userId, addressLine1, addressLine2, 
                                     city, state, zipCode, country, isDefault);
        
        // If this is set as default, remove default from other addresses
        if (isDefault) {
            addressMapper.removeDefaultForUser(userId);
        }
        
        addressMapper.insert(address);
        return address;
    }
    
    public Address updateAddress(Long userId, Long addressId, String addressLine1, String addressLine2,
                                String city, String state, String zipCode, String country,
                                boolean isDefault) {
        Address address = addressMapper.findById(addressId);
        
        if (address == null || !address.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found");
        }
        
        address.setAddressLine1(addressLine1);
        address.setAddressLine2(addressLine2);
        address.setCity(city);
        address.setState(state);
        address.setZipCode(zipCode);
        address.setCountry(country);
        address.setDefault(isDefault);
        
        // If this is set as default, remove default from other addresses
        if (isDefault) {
            addressMapper.removeDefaultForUser(userId);
        }
        
        addressMapper.update(address);
        return address;
    }
    
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressMapper.findById(addressId);
        
        if (address == null || !address.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found");
        }
        
        addressMapper.delete(addressId);
    }
    
    public Address getDefaultAddress(Long userId) {
        return addressMapper.findDefaultByUserId(userId);
    }
}