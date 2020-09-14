package com.hw.aggregate.user.model;

import com.hw.aggregate.user.BizUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BizUserDetailsService implements UserDetailsService {

    @Autowired
    BizUserRepo resourceOwnerRepo;

    @Override
    public UserDetails loadUserByUsername(String usernameOrId) {
        try {
            return resourceOwnerRepo.findById(Long.parseLong(usernameOrId)).get();
        } catch (NumberFormatException ex) {
            /**
             * is email
             */
            BizUser oneByEmail = resourceOwnerRepo.findOneByEmail(usernameOrId);
            if (oneByEmail == null)
                throw new UsernameNotFoundException("unable to find user :: " + usernameOrId);
            return oneByEmail;
        }
    }
}
