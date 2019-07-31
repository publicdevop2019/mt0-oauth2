package com.hw.service;

import com.hw.entity.Client;
import com.hw.repo.ClientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {

    @Autowired
    ClientRepo clientRepo;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Optional<Client> byClientId = clientRepo.findByClientId(clientId);
        if (byClientId.isEmpty())
            return null;
        return byClientId.get();
    }

}
