package com.hw.service;

import com.hw.repo.ResourceOwnerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ResourceOwnerRepo resourceOwnerRepo;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        try {
            return resourceOwnerRepo.findById(Long.parseLong(id)).get();
        } catch (NumberFormatException ex) {
            /**
             * is email
             */
            return resourceOwnerRepo.findOneByEmail(id);
        }
    }
}
