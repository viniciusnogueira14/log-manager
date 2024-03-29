package com.challenge.logmanager.rest.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogEntryResource {

    private Long code;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "America/Sao_Paulo")
    private Date logDate;
    private String ipAddress;
    private String request;
    private Integer logStatus;
    private String userAgent;

}
