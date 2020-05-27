package de.karlthebee.beebot.rest.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerData {
    private String id;
    private String moduleId;
    private String botId;
    private String name;
    private String description="empty";
}
