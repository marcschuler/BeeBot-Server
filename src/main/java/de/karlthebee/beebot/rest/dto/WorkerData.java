package de.karlthebee.beebot.rest.dto;

import de.karlthebee.beebot.dyn.WebLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerData {
    private String id;
    private String moduleId;
    private String botId;
    private String name;
    private String description;

    private List<WebLog.LogEntry> logs;
}
