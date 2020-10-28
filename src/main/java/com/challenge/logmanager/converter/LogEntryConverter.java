package com.challenge.logmanager.converter;

import com.challenge.logmanager.core.entity.LogEntry;
import com.challenge.logmanager.rest.resource.LogEntryResource;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class LogEntryConverter {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String REMOVE_DOUBLE_QUOTES_REGEX = "^(\"|\")$";

    public LogEntry fromResource(LogEntryResource resource) {
        LogEntry entity = new LogEntry();
        BeanUtils.copyProperties(resource, entity);
        return entity;
    }

    public LogEntryResource fromEntity(LogEntry entity) {
        LogEntryResource resource = new LogEntryResource();
        BeanUtils.copyProperties(entity, resource);
        return resource;
    }

    public Collection<LogEntry> fromResource(Collection<LogEntryResource> resources) {
        Collection<LogEntry> entities = new ArrayList<>();
        for (LogEntryResource resource : resources) {
            entities.add(this.fromResource(resource));
        }

        return entities;
    }

    public Collection<LogEntryResource> fromEntity(Collection<LogEntry> entities) {
        Collection<LogEntryResource> resources = new ArrayList<>();
        for (LogEntry entity : entities) {
            resources.add(this.fromEntity(entity));
        }

        return resources;
    }

    public LogEntry fromLogFileLine(String line) {
        String[] splitted = line.split("\\|");

        // The splitted size is added by 1 due to the "entity.code" field which is not present at the file
        if (LogEntry.class.getDeclaredFields().length == (splitted.length + 1)) {
            LogEntry entity = new LogEntry();

            DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT);
            DateTime dateTime = DateTime.parse(splitted[0].replaceAll(REMOVE_DOUBLE_QUOTES_REGEX, ""), formatter);
            entity.setDate(dateTime.toDate());

            entity.setIpAddress(splitted[1].replaceAll(REMOVE_DOUBLE_QUOTES_REGEX, ""));
            entity.setRequest(splitted[2].replaceAll(REMOVE_DOUBLE_QUOTES_REGEX, ""));
            entity.setRequestStatus(Integer.valueOf(splitted[3]));
            entity.setUserAgent(splitted[4].replaceAll(REMOVE_DOUBLE_QUOTES_REGEX, ""));

            return entity;
        } else {
            throw new IllegalArgumentException("There are lines with invalid format in the log.");
        }
    }

    public Collection<LogEntry> fromLogFile(Collection<String> lines) {
        Collection<LogEntry> entities = new ArrayList<>();
        for (String line : lines) {
            entities.add(this.fromLogFileLine(line));
        }
        return entities;
    }

    public Slice<LogEntryResource> fromEntity(Slice<LogEntry> entities) {
        return entities.map(this::fromEntity);
    }
}
