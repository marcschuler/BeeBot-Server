package de.karlthebee.beebot.module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Status {
    private boolean okay;
    private String message;
}
