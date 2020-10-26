package com.challenge.logmanager.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "LOG_ENTRY")
@NoArgsConstructor
@AllArgsConstructor
public class LogEntry {

    @Id
    @GeneratedValue(generator = "LogEntrySeq")
    @SequenceGenerator(name = "LogEntrySeq", sequenceName = "log_entry_id_log_entry_seq", allocationSize = 1)
    @Column(name = "ID_LOG_ENTRY", unique = true, nullable = false, precision = 18)
    private Long code;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_LOG_ENTRY")
    private Date date;

    @Column(name = "NR_IP_LOG", length = 15, nullable = false)
    private String ipAddress;

    @Column(name = "TX_REQUEST_LOG", length = 20, nullable = false)
    private String request;

    @Column(name = "NR_LOG_STATUS", nullable = false, precision = 18)
    private Integer requestStatus;

    @Column(name = "TX_USER_AGENT_LOG", length = 1000, nullable = false)
    private String userAgent;

}
