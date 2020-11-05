package com.challenge.logmanager.rest.controller;

import com.challenge.logmanager.rest.resource.LogEntryResource;
import com.challenge.logmanager.service.LogEntryService;
import com.challenge.logmanager.specification.LogEntrySpecification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping(value = "/log")
public class LogManagerController {

    private final LogEntryService logEntryService;

    public LogManagerController(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<LogEntryResource>> getGetAll() {
        return new ResponseEntity<>(logEntryService.findAll(), HttpStatus.OK);
    }

    @GetMapping
    public Slice<LogEntryResource> get(LogEntrySpecification filter, Pageable pageable) {
        return logEntryService.findByFilter(filter, pageable);
    }

    @PostMapping
    public ResponseEntity<LogEntryResource> create(@RequestBody LogEntryResource resource) {
        return new ResponseEntity<>(logEntryService.create(resource), HttpStatus.CREATED);
    }

    @PostMapping(value = "/batch")
    public ResponseEntity<String> importFromFile(@RequestParam("content") MultipartFile content) {
        try {
            int rowsCreated = logEntryService.importFromFile(content);
            return ResponseEntity.created(URI.create("")).body(String.format("%s log rows were imported from the file.", rowsCreated));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAll() {
        logEntryService.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{code}")
    public ResponseEntity<String> deleteOne(@PathVariable(value = "code") Long code) {
        try {
            logEntryService.delete(code);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

}
