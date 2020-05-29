package de.karlthebee.beebot.dyn;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class WebLog {
    private static final int MAX_ENTRIES = 50;


    private List<LogEntry> logEntries = new ArrayList<>();

    public void of(String message, LogType type) {
        var wl = of(message);
        wl.setType(type);
    }

    public void error(String s) {
        of(s, LogType.ERROR);
    }

    public void warning(String s) {
        of(s, LogType.WARNING);
    }

    public LogEntry of(String message) {
        var wl = new LogEntry();
        wl.setMessage(message);
        logEntries.add(wl);
        if (logEntries.size() > MAX_ENTRIES)
            logEntries.remove(0);
        return wl;
    }

    public void info(String message) {
        of(message);
    }


    @Data
    public class LogEntry {
        private LocalDateTime datetime = LocalDateTime.now();
        private LogType type = LogType.INFO;
        private String message;

    }

    public enum LogType {
        INFO,
        WARNING,
        ERROR
    }
}


