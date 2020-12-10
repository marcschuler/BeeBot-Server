package de.karlthebee.beebot.service;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.karlthebee.beebot.rest.dto.ClientDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DtoService {

    private final IPService ipService;

    /**
     * Converts an Client to an ClientDTO
     *
     * @param client the client
     * @return the client DTO
     */
    public ClientDTO toClientDTO(Client client) {
        return ClientDTO.builder()
                .id(client.getId())
                .nickname(client.getNickname())
                .ip(client.getIp())
                .platform(client.getPlatform())
                .version(client.getVersion())
                .country(client.getCountry())
                .countryFlag(ipService.countryCodeToEmoji(client.getCountry()))
                .query(client.isServerQueryClient())
                .afk(client.isAway())
                .build();
    }
}
