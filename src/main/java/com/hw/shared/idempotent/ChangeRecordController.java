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

    @DeleteMapping("root")
    public ResponseEntity<?> deleteForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam) {
        rootChangeRecordApplicationService.deleteByQuery(queryParam);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("app")
    public ResponseEntity<?> deleteForAppByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam) {
        appChangeRecordApplicationService.deleteByQuery(queryParam);
        return ResponseEntity.ok().build();
    }

}
