package de.karlthebee.beebot.module.ctafk.data;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class CaptureTheAFKDay {
    private LocalDate day;
    private Map<String, Long> afkdays = new HashMap<>();
}
