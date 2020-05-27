package de.karlthebee.beebot.data;

import lombok.Data;

@Data
public class ServerState {
    private String id;

    private boolean online;
    private TeamspeakConfig teamspeakConfig;
}
