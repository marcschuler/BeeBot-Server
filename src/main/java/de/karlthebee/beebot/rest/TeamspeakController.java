package de.karlthebee.beebot.rest;

import com.github.theholywaffle.teamspeak3.TS3Api;
import de.karlthebee.beebot.rest.dto.ChannelReference;
import de.karlthebee.beebot.rest.dto.ClientDTO;
import de.karlthebee.beebot.rest.dto.ClientReference;
import de.karlthebee.beebot.rest.dto.GroupReference;
import de.karlthebee.beebot.service.DtoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Sends basic data directly from the teamspeak server
 * TODO implement rate-limiting
 */
@Slf4j
@RestController
@RequestMapping("beebot/{bid}")
@CrossOrigin
@AllArgsConstructor
public class TeamspeakController extends RestUtil {

    private final DtoService dtoService;


    @GetMapping("channels")
    public List<ChannelReference> channels(@PathVariable("bid") String bid) {
        requireToken();
        var bot = botById(bid);
        var channels = bot.withApi().map(TS3Api::getChannels).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not get channels"));
        return channels.stream().map(c -> new ChannelReference(c.getId(), c.getName())).collect(Collectors.toList());
    }

    @GetMapping("clients")
    public List<ClientReference> clients(@PathVariable("bid") String bid) {
        requireToken();
        var bot = botById(bid);
        var clients = bot.withApi().map(TS3Api::getClients).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not get clients"));
        return clients.stream().map(c -> new ClientReference(c.getId(), c.getNickname())).collect(Collectors.toList());
    }

    @GetMapping("clients/dto")
    public List<ClientDTO> clientsDto(@PathVariable("bid") String bid) {
        requireToken();
        var bot = botById(bid);
        var clients = bot.withApi().map(TS3Api::getClients)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not get clients"));
        return clients.stream().map(dtoService::toClientDTO)
                .collect(Collectors.toList());
    }


    @GetMapping("groups/server")
    public List<GroupReference> groupsServer(@PathVariable("bid") String bid) {
        requireToken();
        var bot = botById(bid);
        var serverGroups = bot.withApi().map(TS3Api::getServerGroups).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not get server groups"));
        return serverGroups.stream().map(g -> new GroupReference(g.getId(), g.getName())).collect(Collectors.toList());
    }

    @GetMapping("groups/channel")
    public List<GroupReference> groupsChannel(@PathVariable("bid") String bid) {
        requireToken();
        var bot = botById(bid);
        var serverGroups = bot.withApi().map(TS3Api::getChannelGroups).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not get server groups"));
        return serverGroups.stream().map(g -> new GroupReference(g.getId(), g.getName())).collect(Collectors.toList());
    }
}
