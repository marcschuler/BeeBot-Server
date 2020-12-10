package de.karlthebee.beebot.data;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Config for a worker including all IDs, the (generic) data and dates
 * @param <T> the config data class
 */
@Data
public class WorkerConfig<T> {
    @Id
    private String id;
    @NotNull
    private String moduleId;
    @NotNull
    private String botId;
    @NotNull
    private T data;

    @CreatedDate
    private LocalDateTime created;

    @LastModifiedDate
    private LocalDateTime modified;
}
