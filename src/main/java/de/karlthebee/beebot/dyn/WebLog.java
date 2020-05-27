package de.karlthebee.beebot.dyn;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WebLog {
    public static WebLog of(String message, LogType type){
        var wl = of(message);
        wl.setType(type);
        return wl;
    }

    public static WebLog error(String s) {
        return of(s,LogType.ERROR);
    }

    public static WebLog warning(String s) {
        return of(s,LogType.WARNING);
    }

    public static WebLog of(String message){
        var wl = new WebLog();
        wl.setMessage(message);
        return wl;
    }

    private LocalDateTime datetime = LocalDateTime.now();
    private LogType type = LogType.INFO;
    private String message;




    public enum LogType {
        INFO,
        WARNING,
        ERROR
    }
}


