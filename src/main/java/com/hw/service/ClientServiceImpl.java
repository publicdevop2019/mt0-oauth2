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

@Service
@Slf4j
public class ClientServiceImpl {

    @Autowired
    ClientRepo clientRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    TokenRevocationService<Client> tokenRevocationService;

    /**
     * if client is marked as resource then it must be a backend and first party application
     */
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

    public Map<String, String> readPartialClientById(Long id, String field) {
        Optional<Client> byId = clientRepo.findById(id);
        if (byId.isEmpty())
            throw new BadRequestException("unable to find client");
        if (field == null) {
            throw new BadRequestException("field should not be empty");
        } else {
            if ("autoApprove".equals(field)) {
                HashMap<String, String> stringStringHashMap = new HashMap<>();
                /**
                 * if autoApprove is null, it won't be included in response
                 * due to Jackson configured to ignore null fields
                 */
                if (null == byId.get().getAutoApprove()) {
                    stringStringHashMap.put("autoApprove", Boolean.FALSE.toString());
                } else {
                    stringStringHashMap.put("autoApprove", byId.get().getAutoApprove().toString());
                }
                return stringStringHashMap;
            } else {
                throw new BadRequestException("unsupported fields");
            }
        }
    }

    /**
     * replace an existing client, if no change to pwd then send empty
     *
     * @param client
     * @param id
     * @return
     */
    public void replaceClient(Client client, Long id) {
        validateResourceIndicator(client);
        validateResourceId(client);
        Optional<Client> oAuthClient1 = clientRepo.findById(id);
        if (oAuthClient1.isEmpty()) {
            throw new BadRequestException("unable to find client");
        } else {
            Client client2 = oAuthClient1.get();
            String oldClientId = client2.getClientId();
            boolean b = tokenRevocationService.shouldRevoke(client2, client);

            if (StringUtils.hasText(client.getClientSecret())) {
                client.setClientSecret(encoder.encode(client.getClientSecret()));
            } else {
                client.setClientSecret(oAuthClient1.get().getClientSecret());
            }
            /**
             * copy to prevent new id gen, below method rely on correct following java conventions
             * setter & getter should return same type
             */
            BeanUtils.copyProperties(client, client2);
            clientRepo.save(client2);
            /** only revoke token after change has been persisted*/
            tokenRevocationService.blacklist(oldClientId, b);
        }
    }

    /**
     * root client can not be deleted
     */
    public void deleteClient(Long id) {
        preventRootAccountChange(id);
        Optional<Client> oAuthClient1 = clientRepo.findById(id);
        if (oAuthClient1.isEmpty())
            throw new BadRequestException("unable to find client");
        clientRepo.delete(oAuthClient1.get());
        tokenRevocationService.blacklist(oAuthClient1.get().getClientId(), true);
    }

    private void validateResourceId(Client client) throws IllegalArgumentException {
        /**
         * selected resource ids should be eligible resource
         */
        if (client.getResourceIds() == null || client.getResourceIds().size() == 0
                || client.getResourceIds().stream().anyMatch(resourceId -> clientRepo.findByClientId(resourceId).isEmpty()
                || !clientRepo.findByClientId(resourceId).get().getResourceIndicator()))
            throw new BadRequestException("invalid resourceId found");
    }

    private void validateResourceIndicator(Client client) throws IllegalArgumentException {
        if (client.getResourceIndicator())
            if (client.getGrantedAuthorities().stream().anyMatch(e -> e.getGrantedAuthority().equals(ClientAuthorityEnum.ROLE_BACKEND))
                    && client.getGrantedAuthorities().stream().anyMatch(e -> e.getGrantedAuthority().equals(ClientAuthorityEnum.ROLE_FIRST_PARTY))) {
            } else {
                throw new BadRequestException("invalid grantedAuthorities to be a resource, must be ROLE_FIRST_PARTY & ROLE_BACKEND");
            }

    }

    private void preventRootAccountChange(Long id) throws AccessDeniedException {
        Optional<Client> byId = clientRepo.findById(id);
        if (!byId.isEmpty() && byId.get().getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new BadRequestException("root client can not be deleted");
    }
}
