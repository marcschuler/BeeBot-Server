package de.karlthebee.beebot.module.ctafk.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptureTheAFKDay {
    private LocalDate day;
    private Map<String, Long> afkdays = new HashMap<>();
}
