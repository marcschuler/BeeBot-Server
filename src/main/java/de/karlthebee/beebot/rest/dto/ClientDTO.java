package de.karlthebee.beebot.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {
    private int id;
    private String nickname;
    private boolean query;

    private String ip;
    private String platform;
    private String version;
    private String country;
    private String countryFlag;

    private boolean afk;
}
