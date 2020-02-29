package com.hw.service;

import com.hw.entity.ResourceOwner;
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
    public UserDetails loadUserByUsername(String usernameOrId) throws UsernameNotFoundException {
        try {
            return resourceOwnerRepo.findById(Long.parseLong(usernameOrId)).get();
        } catch (NumberFormatException ex) {
            /**
             * is email
             */
            ResourceOwner oneByEmail = resourceOwnerRepo.findOneByEmail(usernameOrId);
            if (oneByEmail == null)
                throw new UsernameNotFoundException("unable to find user :: " + usernameOrId);
            return oneByEmail;
        }
    }
}
