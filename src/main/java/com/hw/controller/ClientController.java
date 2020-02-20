package com.hw.controller;

import com.hw.entity.Client;
import com.hw.service.ClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("v1/api")
public class ClientController {

    @Autowired
    ClientServiceImpl clientService;

    @PostMapping("clients")
    public ResponseEntity<?> createClient(@Valid @RequestBody Client client) {
        return ResponseEntity.ok().header("Location", String.valueOf(clientService.createClient(client).getId())).build();
    }

    @GetMapping("clients")
    public List<Client> readClients() {
        return clientService.readClients();
    }

    @Deprecated
    @GetMapping("clients/autoApprove")
    public ResponseEntity<?> readClient(@RequestParam(name = "clientId") String clientId) {
        Client client = clientService.readClient(clientId);
        HashMap<String, Boolean> stringBooleanHashMap = new HashMap<>();
        /**
         * if autoApprove is null, it won't be included in response
         * due to Jackson configured to ignore null fields
         */
        stringBooleanHashMap.put("autoApprove", client.getAutoApprove());
        return ResponseEntity.ok(stringBooleanHashMap);
    }

    @GetMapping("clients/{id}")
    public ResponseEntity<?> readPartialClientById(@PathVariable Long id, @RequestParam(name = "field") String field) {
        return ResponseEntity.ok(clientService.readPartialClientById(id, field));
    }

    @PutMapping("clients/{id}")
    public ResponseEntity<?> replaceClient(@Valid @RequestBody Client client, @PathVariable Long id) {
        clientService.replaceClient(client, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("clients/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok().build();
    }


}
