package com.challenge.logmanager.service;

import com.challenge.logmanager.converter.LogEntryConverter;
import com.challenge.logmanager.core.entity.LogEntry;
import com.challenge.logmanager.core.repository.LogEntryRepository;
import com.challenge.logmanager.enumeration.PeriodEnum;
import com.challenge.logmanager.rest.resource.LogEntryResource;
import com.challenge.logmanager.specification.LogEntrySpecification;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Predicate;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Service
public class LogEntryService {

    private final LogEntryRepository repository;
    private final LogEntryConverter converter;

    public LogEntryService(LogEntryRepository logEntryRepository, LogEntryConverter logEntryConverter) {
        this.repository = logEntryRepository;
        this.converter = logEntryConverter;
    }

    public LogEntryResource create(LogEntryResource resource) {
        LogEntry entity = converter.fromResource(resource);
        return converter.fromEntity(repository.save(entity));
    }

    public Collection<LogEntryResource> findAll() {
        return converter.fromEntity(repository.findAll());
    }

    public Integer importFromFile(MultipartFile content) throws IOException {
        InputStream inputStream = content.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        Stream<String> lines = reader.lines();
        Collection<LogEntry> entities = converter.fromLogFile(lines.collect(Collectors.toList()));

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

    public Slice<LogEntryResource> findByFilter(LogEntrySpecification filter, Pageable pageable) throws ParseException {
        Slice<LogEntry> entities;
        if (Objects.nonNull(filter) &&
                (Objects.nonNull(filter.getIpAddress())
                        || Objects.nonNull(filter.getLogDate())
                        || Objects.nonNull(filter.getUserAgent()))) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            LogEntry search = LogEntry.builder()
                    .ipAddress(filter.getIpAddress())
                    .userAgent(filter.getUserAgent())
                    .build();

            ExampleMatcher matcher = ExampleMatcher
                    .matching()
                    .withMatcher("ipAddress", exact())
                    .withMatcher("userAgent", contains().ignoreCase());

            if (Objects.nonNull(filter.getLogDate()) && Objects.nonNull(filter.getDatePeriod())) {
                entities = repository.findAll(this.getSpecificationWithDate(Example.of(search, matcher),
                        format.parse(filter.getLogDate()), filter.getDatePeriod()), pageable);
            } else {
                matcher.withMatcher("logDate", exact());
                entities = repository.findAll(Example.of(search, matcher), pageable);
            }
        } else {
            entities = repository.findAll(pageable);
        }
        return converter.fromEntity(entities);
    }

    private Specification<LogEntry> getSpecificationWithDate(Example<LogEntry> example, Date logDate, String period) {
        return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(period)) {
                if (period.equalsIgnoreCase(PeriodEnum.BEFORE.getArconym())) {
                    predicates.add(builder.lessThanOrEqualTo(root.get("logDate"), logDate));
                } else if (period.equalsIgnoreCase(PeriodEnum.AFTER.getArconym())) {
                    predicates.add(builder.greaterThanOrEqualTo(root.get("logDate"), logDate));
                } else if (period.equalsIgnoreCase(PeriodEnum.EQUALS.getArconym())) {
                    predicates.add(builder.equal(root.get("logDate"), logDate));
                }
            }

            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
