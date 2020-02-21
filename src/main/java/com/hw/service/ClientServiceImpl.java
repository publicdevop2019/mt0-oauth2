package com.hw.service;

import com.hw.clazz.eenum.ClientAuthorityEnum;
import com.hw.entity.Client;
import com.hw.interfaze.TokenRevocationService;
import com.hw.repo.ClientRepo;
import com.hw.shared.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
@Service
@Slf4j
public class ClientServiceImpl {

    @Autowired
    ClientRepo clientRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    TokenRevocationService<Client> tokenRevocationService;

    public Client createClient(Client client) {
        validateResourceId(client);
        validateResourceIndicator(client);
        Optional<Client> clientId = clientRepo.findByClientId(client.getClientId());
        if (clientId.isEmpty()) {
            if (null == client.getClientSecret()) {
                client.setClientSecret(encoder.encode(""));
            } else {
                client.setClientSecret(encoder.encode(client.getClientSecret().trim()));
            }
            return clientRepo.save(client);
        } else {
            throw new BadRequestException("client already exist");
        }
    }

    public List<Client> readClients() {
        return clientRepo.findAll();
    }

    /**
     * only clientId is available, value can be read with client/{id}
     */
    @Deprecated
    public Client readClient(String clientId) {
        Optional<Client> byClientId = clientRepo.findByClientId(clientId);
        if (byClientId.isEmpty())
            throw new BadRequestException("unable to find client");
        return byClientId.get();
    }

    /**
     * if autoApprove is null, it won't be included in response
     * due to Jackson configured to ignore null fields
     */
    public Map<String, String> readPartialClientById(Long id, String field) {
        if (!"autoApprove".equals(field))
            throw new BadRequestException("field not supported");
        Client clientById = getClientById(id);
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        if (null == clientById.getAutoApprove()) {
            stringStringHashMap.put("autoApprove", Boolean.FALSE.toString());
        } else {
            stringStringHashMap.put("autoApprove", clientById.getAutoApprove().toString());
        }
        return stringStringHashMap;
    }

    /**
     * replace an existing client, if no change to pwd then send empty
     * note : copy to prevent new id gen, below method rely on correct following java conventions
     * setter & getter should return same type
     * only revoke token after change has been persisted
     */
    public void replaceClient(Client client, Long id) {
        validateResourceIndicator(client);
        validateResourceId(client);
        Client clientById = getClientById(id);
        String oldClientId = clientById.getClientId();
        boolean b = tokenRevocationService.shouldRevoke(clientById, client);
        if (StringUtils.hasText(client.getClientSecret())) {
            client.setClientSecret(encoder.encode(client.getClientSecret()));
        } else {
            client.setClientSecret(clientById.getClientSecret());
        }
        BeanUtils.copyProperties(client, clientById);
        clientRepo.save(clientById);
        tokenRevocationService.blacklist(oldClientId, b);
    }

    public void deleteClient(Long id) {
        preventRootAccountChange(id);
        Client clientById = getClientById(id);
        clientRepo.delete(clientById);
        tokenRevocationService.blacklist(clientById.getClientId(), true);
    }

    private Client getClientById(Long id) {
        Optional<Client> optionalClient = clientRepo.findById(id);
        if (optionalClient.isEmpty())
            throw new BadRequestException("unable to find client");
        return optionalClient.get();
    }

    /**
     * selected resource ids should be eligible resource
     */
    private void validateResourceId(Client client) throws IllegalArgumentException {
        if (client.getResourceIds() == null || client.getResourceIds().size() == 0
                || client.getResourceIds().stream().anyMatch(resourceId -> clientRepo.findByClientId(resourceId).isEmpty()
                || !clientRepo.findByClientId(resourceId).get().getResourceIndicator()))
            throw new BadRequestException("invalid resourceId found");
    }

    /**
     * if client is marked as resource then it must be a backend and first party application
     */
    private void validateResourceIndicator(Client client) throws IllegalArgumentException {
        if (client.getResourceIndicator())
            if (client.getGrantedAuthorities().stream().noneMatch(e -> e.getGrantedAuthority().equals(ClientAuthorityEnum.ROLE_BACKEND))
                    || client.getGrantedAuthorities().stream().noneMatch(e -> e.getGrantedAuthority().equals(ClientAuthorityEnum.ROLE_FIRST_PARTY)))
                throw new BadRequestException("invalid grantedAuthorities to be a resource, must be ROLE_FIRST_PARTY & ROLE_BACKEND");
    }

    /**
     * root client can not be deleted
     */
    private void preventRootAccountChange(Long id) throws AccessDeniedException {
        Optional<Client> byId = clientRepo.findById(id);
        if (byId.isPresent() && byId.get().getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new BadRequestException("root client can not be deleted");
    }
}
