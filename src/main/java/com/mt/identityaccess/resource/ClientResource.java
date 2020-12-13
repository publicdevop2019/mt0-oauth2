package com.mt.identityaccess.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.shared.sql.SumPagedRep;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.application.ClientApplicationService;
import com.mt.identityaccess.application.command.ProvisionClientCommand;
import com.mt.identityaccess.application.command.ReplaceClientCommand;
import com.mt.identityaccess.application.representation.RootClientCardRepresentation;
import com.mt.identityaccess.application.representation.RootClientRepresentation;
import com.mt.identityaccess.application.representation.UserBizClientCardRep;
import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.domain.model.client.ClientId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "clients")
public class ClientResource {

    @PostMapping("root")
    public ResponseEntity<Void> createForRoot(@RequestBody ProvisionClientCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ClientId clientId = clientApplicationService().provisionClient(command, changeId);
        return ResponseEntity.ok().header("Location", clientId.id()).build();
    }

    @GetMapping("root")
    public ResponseEntity<SumPagedRep<RootClientCardRepresentation>> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        SumPagedRep<Client> clients = clientApplicationService().clients(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep(clients, RootClientCardRepresentation::new));
    }

    @GetMapping("root/{id}")
    public ResponseEntity<RootClientRepresentation> readForRootById(@PathVariable String id) {
        Optional<Client> client = clientApplicationService().client(id);
        return client.map(value -> ResponseEntity.ok(new RootClientRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PutMapping("root/{id}")
    public ResponseEntity<Void> replaceForRootById(@PathVariable(name = "id") String id, @RequestBody ReplaceClientCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        clientApplicationService().replaceClient(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("root/{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        clientApplicationService().removeClient(id, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("root")
    public ResponseEntity<Void> deleteForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        clientApplicationService().removeClients(queryParam, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "root/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable(name = "id") String id, @RequestBody JsonPatch command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        clientApplicationService().patchClient(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("user")
    public ResponseEntity<SumPagedRep<UserBizClientCardRep>> getForUserByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                               @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                               @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        SumPagedRep<Client> clients = clientApplicationService().clients(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep(clients, UserBizClientCardRep::new));
    }

    private ClientApplicationService clientApplicationService() {
        return ApplicationServiceRegistry.clientApplicationService();
    }
}
