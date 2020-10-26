package com.challenge.logmanager.service;

import com.challenge.logmanager.assembler.LogEntryAssembler;
import com.challenge.logmanager.core.entity.LogEntry;
import com.challenge.logmanager.core.repository.LogEntryRepository;
import com.challenge.logmanager.dto.LogEntryResource;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LogEntryService {

    private final LogEntryRepository repository;
    private final LogEntryAssembler assembler;

    public LogEntryService(LogEntryRepository logEntryRepository, LogEntryAssembler logEntryAssembler) {
        this.repository = logEntryRepository;
        this.assembler = logEntryAssembler;
    }

    public LogEntryResource create(LogEntryResource resource) {
        LogEntry entity = assembler.fromResource(resource);
        return assembler.fromEntity(repository.save(entity));
    }

    public Collection<LogEntryResource> findAll() {
        return assembler.fromEntity(repository.findAll());
    }

    public Integer importFromFile(String path) throws IOException, IllegalArgumentException {
        FileInputStream fileInputStream = new FileInputStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

        Stream<String> lines = reader.lines();
        Collection<LogEntry> entities = assembler.fromLogFile(lines.collect(Collectors.toList()));

        reader.close();
        Collection<LogEntry> persisted = repository.saveAll(entities);

        return persisted.size();
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void delete(Long code) {
        Optional<LogEntry> entity = repository.findById(code);
        if (entity.isPresent()) {
            repository.delete(entity.get());
        } else {
            throw new EntityNotFoundException("No log found with the code " + code);
        }
    }
}
