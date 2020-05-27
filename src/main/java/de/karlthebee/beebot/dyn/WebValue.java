package de.karlthebee.beebot.dyn;

import lombok.Data;

@Data
public class WebValue {
    private String name;
    private Object defaultValue;
    private String type;
    private String description;
}
