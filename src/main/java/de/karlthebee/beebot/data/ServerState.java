package de.karlthebee.beebot.data;

import lombok.Data;

/**
 * The current state of the server
 */
@Data
public class ServerState {
    private String id;

    private boolean online;
    private TeamspeakConfig teamspeakConfig;
}
