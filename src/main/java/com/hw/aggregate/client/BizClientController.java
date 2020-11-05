package com.hw.aggregate.client;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.client.command.RootCreateBizClientCommand;
import com.hw.aggregate.client.command.RootUpdateBizClientCommand;
import com.hw.aggregate.client.representation.RootBizClientCardRep;
import com.hw.aggregate.client.representation.RootBizClientRep;
import com.hw.aggregate.client.representation.UserBizClientCardRep;
import com.hw.shared.sql.SumPagedRep;
import com.hw.shared.validation.BizValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "clients")
public class BizClientController {

    @Autowired
    private RootBizClientApplicationService rootClientApplicationService;

    @Autowired
    private AppBizClientApplicationService appBizClientApplicationService;

    @Autowired
    private UserBizClientApplicationService userBizClientApplicationService;
    @Autowired
    BizValidator validator;

    @PostMapping("root")
    public ResponseEntity<Void> createForRoot(@RequestBody RootCreateBizClientCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("rootCreateBizClientCommand", command);
        return ResponseEntity.ok().header("Location", String.valueOf(rootClientApplicationService.create(command, changeId).getId())).build();
    }

    @GetMapping("root")
    public ResponseEntity<SumPagedRep<RootBizClientCardRep>> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        return ResponseEntity.ok(rootClientApplicationService.readByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("root/{id}")
    public ResponseEntity<RootBizClientRep> readForRootById(@PathVariable Long id) {
        return ResponseEntity.ok(rootClientApplicationService.readById(id));
    }

    @PutMapping("root/{id}")
    public ResponseEntity<Void> replaceForRootById(@PathVariable(name = "id") Long id, @RequestBody RootUpdateBizClientCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("rootUpdateBizClientCommand", command);
        rootClientApplicationService.replaceById(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("root/{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable Long id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        rootClientApplicationService.deleteById(id, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("root")
    public ResponseEntity<Void> deleteForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        rootClientApplicationService.deleteByQuery(queryParam, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("user")
    public ResponseEntity<SumPagedRep<UserBizClientCardRep>> getForUserByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                               @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                               @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        return ResponseEntity.ok(userBizClientApplicationService.readByQuery(queryParam, pageParam, skipCount));
    }

    @PatchMapping(path = "root/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(HTTP_HEADER_CHANGE_ID, changeId);
        rootClientApplicationService.patchById(id, patch, params);
        return ResponseEntity.ok().build();
    }
}
