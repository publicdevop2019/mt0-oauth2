package com.mt.identityaccess.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.sql.SumPagedRep;
import com.mt.common.validate.BizValidator;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.application.endpoint.command.EndpointCreateCommand;
import com.mt.identityaccess.application.endpoint.command.EndpointUpdateCommand;
import com.mt.identityaccess.application.endpoint.representation.EndpointCardRepresentation;
import com.mt.identityaccess.application.endpoint.representation.EndpointRepresentation;
import com.mt.identityaccess.domain.model.endpoint.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "endpoints")
public class EndpointResource {

    @Autowired
    BizValidator validator;

    @PostMapping("root")
    public ResponseEntity<Void> createForRoot(@RequestBody EndpointCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("rootCreateBizEndpointCommand", command);
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.endpointApplicationService().create(command, changeId)).build();
    }

    @GetMapping("root")
    public ResponseEntity<SumPagedRep<EndpointCardRepresentation>> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                      @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                      @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<Endpoint> endpoints = ApplicationServiceRegistry.endpointApplicationService().endpoints(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep(endpoints, EndpointCardRepresentation::new));
    }

    @GetMapping("root/{id}")
    public ResponseEntity<EndpointRepresentation> readForRootById(@PathVariable String id) {
        Optional<Endpoint> endpoint = ApplicationServiceRegistry.endpointApplicationService().endpoint(id);
        return endpoint.map(value -> ResponseEntity.ok(new EndpointRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PutMapping("root/{id}")
    public ResponseEntity<Void> replaceForRootById(@RequestBody EndpointUpdateCommand command,
                                                   @PathVariable String id,
                                                   @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("rootUpdateBizEndpointCommand", command);
        ApplicationServiceRegistry.endpointApplicationService().update(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("root/{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.endpointApplicationService().removeEndpoint(id, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("root")
    public ResponseEntity<Void> deleteForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                      @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.endpointApplicationService().removeEndpoints(queryParam, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "root/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable(name = "id") String id,
                                                 @RequestBody JsonPatch patch,
                                                 @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {

        ApplicationServiceRegistry.endpointApplicationService().patchEndpoint(id, patch, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "root/event/reload")
    public ResponseEntity<Void> postForRoot(@RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.endpointApplicationService().reloadEndpointCache(changeId);
        return ResponseEntity.ok().build();
    }
}
