package de.karlthebee.beebot.module.ctafk.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Document
public class CaptureTheAFKData {
    @Id
    private String id;

    //all collected days
    private List<CaptureTheAFKDay> days = new ArrayList<>();

    //save all known names - teamspeak may not provide them to us :(
    private Map<String, String> knownNames = new HashMap<>();
}
