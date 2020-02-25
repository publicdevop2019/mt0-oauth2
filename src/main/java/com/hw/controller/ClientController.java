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


    /**
     * used when only clientId is available
     */
    @GetMapping("clients/autoApprove")
    public ResponseEntity<?> autoApprove(@RequestParam(name = "clientId") String clientId) {
        Client client = clientService.readClientByClientId(clientId);
        HashMap<String, Boolean> stringBooleanHashMap = new HashMap<>();
        stringBooleanHashMap.put("autoApprove", client.getAutoApprove());
        return ResponseEntity.ok(stringBooleanHashMap);
    }

    /**
     * used when only id is available
     */
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
