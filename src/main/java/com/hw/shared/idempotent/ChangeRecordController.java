package com.hw.shared.idempotent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "changes")
public class ChangeRecordController {
    @Autowired
    private RootChangeRecordApplicationService rootChangeRecordApplicationService;
    @Autowired
    private AppChangeRecordApplicationService appChangeRecordApplicationService;

//    @PostMapping("app")
//    public ResponseEntity<?> createForApp(@RequestBody CreateChangeRecordCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
//        return ResponseEntity.ok().header("Location", String.valueOf(appChangeRecordApplicationService.create(command, changeId).getId())).build();
//    }

    @GetMapping("root")
    public ResponseEntity<?> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        return ResponseEntity.ok(rootChangeRecordApplicationService.readByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("root/{id}")
    public ResponseEntity<?> readForRootById(@PathVariable Long id) {
        return ResponseEntity.ok(rootChangeRecordApplicationService.readById(id));
    }

    @DeleteMapping("root/{id}")
    public ResponseEntity<?> deleteForRootById(@PathVariable Long id) {
        rootChangeRecordApplicationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

//    @DeleteMapping("app/{id}")
//    public ResponseEntity<?> deleteForAppById(@PathVariable Long id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
//        appChangeRecordApplicationService.deleteById(id, changeId);
//        return ResponseEntity.ok().build();
//    }

//    @DeleteMapping("root")
//    public ResponseEntity<?> deleteForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
//        rootChangeRecordApplicationService.deleteByQuery(queryParam, changeId);
//        return ResponseEntity.ok().build();
//    }

}
