package com.hw.aggregate.client.model;

import com.hw.aggregate.client.BizClientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BizClientDetailsService implements ClientDetailsService {

    @Autowired
    BizClientRepo clientRepo;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Optional<BizClient> byClientId = clientRepo.findById(Long.parseLong(clientId));
        if (byClientId.isEmpty())
            return null;
        return byClientId.get();
    }

}
