package com.hw.aggregate.client;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.client.command.CreateClientCommand;
import com.hw.aggregate.client.command.UpdateClientCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "clients")
public class BizClientController {

    @Autowired
    private RootBIzClientApplicationService rootClientApplicationService;

    @Autowired
    private UserBizClientApplicationService userClientApplicationService;

    @PostMapping("root")
    public ResponseEntity<?> createForRoot(@RequestBody CreateClientCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", String.valueOf(rootClientApplicationService.create(command, changeId).getId())).build();
    }

    @GetMapping("root")
    public ResponseEntity<?> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        return ResponseEntity.ok(rootClientApplicationService.readByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("root/{id}")
    public ResponseEntity<?> readForRootById(@PathVariable Long id) {
        return ResponseEntity.ok(rootClientApplicationService.readById(id));
    }

    @PutMapping("root/{id}")
    public ResponseEntity<?> replaceForRootById(@PathVariable(name = "id") Long id, @RequestBody UpdateClientCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        rootClientApplicationService.replaceById(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("root/{id}")
    public ResponseEntity<?> deleteForRootById(@PathVariable Long id) {
        rootClientApplicationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("user")
    public ResponseEntity<?> getForUserByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                               @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                               @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        return ResponseEntity.ok(userClientApplicationService.readByQuery(queryParam, pageParam, skipCount));
    }

    @PatchMapping(path = "root/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForRootById(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        rootClientApplicationService.patchById(id, patch, changeId);
        return ResponseEntity.ok().build();
    }
}
