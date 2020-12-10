package de.karlthebee.beebot.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.karlthebee.beebot.Registry;
import de.karlthebee.beebot.Util;
import de.karlthebee.beebot.data.WorkerConfig;
import de.karlthebee.beebot.dyn.WebValue;
import de.karlthebee.beebot.dyn.WebValueDescriptor;
import de.karlthebee.beebot.module.Worker;
import de.karlthebee.beebot.repository.WorkerConfigRepository;
import de.karlthebee.beebot.rest.dto.Violation;
import de.karlthebee.beebot.rest.dto.WorkerData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class ModulesController extends RestUtil {

    @Autowired
    private WorkerConfigRepository workerConfigRepository;

    @GetMapping("modules/list")
    public List<ModuleDefinition> list() {
        return Registry.getInstance().getModules().stream()
                .map(m -> new ModuleDefinition(m.getShortName(), m.getName()))
                .collect(Collectors.toList());
    }


    @GetMapping("beebot/{bid}/modules")
    public List<WorkerData> list(@PathVariable("bid") String bid) {
        requireToken();
        var beebot = botById(bid);
        return beebot.getWorkers().stream()
                .map(w -> new WorkerData(w.getId(), w.getModule().getShortName(),beebot.getId(),  w.getModule().getName(),"",w.getWebLog().getLogEntries()))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param bid the bot id
     * @param mid the module id
     * @return the workerdata of the bot
     */
    @GetMapping("beebot/{bid}/modules/{mid}")
    public WorkerData module(@PathVariable("bid") String bid, @PathVariable("mid")String mid){
        requireToken();
        var beebot = botById(bid);
        return beebot.getWorkers().stream()
                .filter(w -> w.getId().equals(mid))
                .map(w -> new WorkerData(w.getId(), w.getModule().getShortName(),beebot.getId(), w.getModule().getName(),"",w.getWebLog().getLogEntries()))
                .findFirst()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Could not find worker"));
    }

    /**
     *
     * @param bid the bot id
     * @param mid the module id
     * @return the config of the bot (anything)
     */
    @GetMapping("beebot/{bid}/modules/{mid}/config")
    public Object moduleConfig(@PathVariable("bid") String bid, @PathVariable("mid")String mid){
        requireToken();
        var beebot = botById(bid);
        return beebot.getWorkers().stream()
                .filter(w -> w.getId().equals(mid))
                .map(Worker::getConfig)
                .findFirst()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Could not find worker"));
    }

    /**
     *
     * @param bid the bot id
     * @param mid the module id
     * @return the config of the bot (anything)
     */
    @PutMapping("beebot/{bid}/modules/{mid}/config")
    public Object moduleConfigChange(@PathVariable("bid") String bid, @PathVariable("mid")String mid,HttpEntity<String> httpEntity) throws JsonProcessingException {
        requireToken();
        var beebot = botById(bid);
        Worker<?> worker = beebot.workerById(mid)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Could not find worker"));

        //cast entity to config class
        var config = Util.mapper().readValue(httpEntity.getBody(), worker.getModule().getConfigClass());
        var vio = getViolations(config);
        log.info("Config " + config.toString() + " of " + worker.getModule().getConfigClass().toString() + " has vilations " + vio.toString());
        if (vio.size() > 0)
            return vio;

        worker.setConfigUnsafe(config);
        return vio;
    }


    @PutMapping("beebot/{bid}/modules/{srtname}")
    public List<Violation> create(@PathVariable("bid") String bid, @PathVariable("srtname") String srtname, HttpEntity<String> httpEntity) throws IOException, IllegalAccessException, InstantiationException {
        requireToken();

        var module = Registry.getInstance().getModuleByShortName(srtname).orElseThrow();
        var beebot = botById(bid);

        //cast entity to config class
        var config = Util.mapper().readValue(httpEntity.getBody(), module.getConfigClass());
        var vio = getViolations(config);
        log.info("Config " + config.toString() + " of " + module.getConfigClass().toString() + " has vilations " + vio.toString());
        if (vio.size() > 0)
            return vio;

        var workerConfig = new WorkerConfig<>();
        workerConfig.setData(config);
        beebot.createWorker(module, workerConfig);
        return vio;
    }

    @DeleteMapping("beebot/{bid}/modules/{wid}")
    public void delete(@PathVariable("bid") String bid, @PathVariable("wid") String wid) {
        requireToken();
        var bot = botById(bid);
        var worker = bot.workerById(wid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find worker"));

        worker.remove();
    }

    @GetMapping("modules/webvalue/{srtname}")
    public List<WebValue> webValues(@PathVariable("srtname") String shortname) throws IllegalAccessException, InstantiationException {
        requireToken();
        var module = Registry.getInstance().getModuleByShortName(shortname)
                .orElseThrow();
        var list = new ArrayList<WebValue>();
        var cfgClass = module.getConfigClass();
        for (var field : cfgClass.getDeclaredFields()) {
            field.setAccessible(true);
            var webvalue = new WebValue();
            webvalue.setName(field.getName());
            webvalue.setDefaultValue(field.get(cfgClass.newInstance()));
            if (field.isAnnotationPresent(WebValueDescriptor.class)) {
                webvalue.setType(field.getAnnotation(WebValueDescriptor.class).value().toString());
            } else {
                webvalue.setType(field.getType().getSimpleName());
            }

            list.add(webvalue);
        }

        return list;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class ModuleDefinition {
        private String id;
        private String name;
    }
}
