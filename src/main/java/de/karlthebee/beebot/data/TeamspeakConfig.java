package de.karlthebee.beebot.data;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
public class TeamspeakConfig {
    @Id
    private String id;
    @NotNull
    @Size(min = 3, max = 32)
    private String name;
    @NotNull
    @Size(min = 6, max = 64)
    private String host = "127.0.0.1";
    @Size(min = 3, max = 64)
    private String username = "serveradmin";
    @Size(min = 0, max = 128)
    private String password;
    @Min(1)
    private int virtualServer = 1;
    private boolean flood = false;
    @NotNull
    @Size(min = 3, max = 32)
    private String nickname = "BeeBot@karlthebee";

}
