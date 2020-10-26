package com.challenge.logmanager.rest.controller;

import com.challenge.logmanager.dto.LogEntryResource;
import com.challenge.logmanager.service.LogEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping(value = "/log")
public class LogManagerController {

    private final LogEntryService logEntryService;

    public LogManagerController(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }

    @GetMapping
    public ResponseEntity<Collection<LogEntryResource>> getGetAll() {
        return new ResponseEntity<>(logEntryService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LogEntryResource> create(@RequestBody LogEntryResource resource) {
        return new ResponseEntity<>(logEntryService.create(resource), HttpStatus.CREATED);
    }

    @PostMapping(value = "/batch")
    public ResponseEntity importFromFile(@RequestBody String path) {
        try {
            int rowsCreated = logEntryService.importFromFile(path);
            return ResponseEntity.created(URI.create("")).body(String.format("%s log rows were imported from the file.", rowsCreated));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity deleteAll() {
        logEntryService.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{code}")
    public ResponseEntity deleteOne(@PathVariable(value = "code") Long code) {
        try {
            logEntryService.delete(code);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

}
