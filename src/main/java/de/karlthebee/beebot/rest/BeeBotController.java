package de.karlthebee.beebot.rest;

import de.karlthebee.beebot.Registry;
import de.karlthebee.beebot.data.ServerState;
import de.karlthebee.beebot.data.TeamspeakConfig;
import de.karlthebee.beebot.dyn.WebLog;
import de.karlthebee.beebot.repository.ConfigRepository;
import de.karlthebee.beebot.rest.dto.Violation;
import de.karlthebee.beebot.ts3.BeeBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("beebot")
@CrossOrigin
public class BeeBotController extends RestUtil {
    @Autowired
    private AutowireCapableBeanFactory beanFactory;
    @Autowired
    private ConfigRepository configRepository;

    @GetMapping
    public List<ServerState> beebots() {
        requireToken();
        return Registry.getInstance().getBots().stream()
                .map(BeeBot::state)
                .collect(Collectors.toList());
    }

    @GetMapping("{bid}")
    public ServerState beebot(@PathVariable("bid") String bid) {
        requireToken();
        return botById(bid).state();
    }

    @GetMapping("{bid}/config")
    public TeamspeakConfig beebotConfig(@PathVariable("bid") String bid) {
        requireToken();
        return botById(bid).getConfig();
    }

    @PutMapping("{bid}/config")
    public List<Violation> beebotConfig(@PathVariable("bid") String bid, @RequestBody TeamspeakConfig teamspeakConfig) {
        requireToken();
        var bot = botById(bid);
        if (this.getViolations(teamspeakConfig).size() > 0)
            return this.getViolations(teamspeakConfig);
        if (!teamspeakConfig.getId().equals(bot.getConfig().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Could not change bot id");

        bot.changeConfig(teamspeakConfig);
        return new ArrayList<>();
    }


    @PutMapping
    public List<Violation> create(@RequestBody TeamspeakConfig teamspeakConfig) {
        requireToken();
        log.info("Create server request");
        var vio = getViolations(teamspeakConfig);
        if (vio.size() > 0)
            return vio;

        log.info("Creating server");

        configRepository.save(teamspeakConfig);

        var bot = new BeeBot(teamspeakConfig);
        beanFactory.autowireBean(bot);
        bot.init();
        return new ArrayList<>();
    }


    @DeleteMapping("{bid}")
    public void remove(@PathVariable("bid") String bid) {
        requireToken();
        var bot = botById(bid);
        bot.close();
        configRepository.delete(bot.getConfig());
    }

    @GetMapping("{bid}/logs")
    public List<WebLog.LogEntry> logs(@PathVariable("bid") String bid) {
        return botById(bid).getWebLog().getLogEntries();
    }
}
