package de.karlthebee.beebot;

import de.karlthebee.beebot.module.Module;
import de.karlthebee.beebot.ts3.BeeBot;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Collects runtime data like all bots and a list of all modules
 */
@Getter
@Slf4j
public class Registry {
    //The file containing the admin token TODO implement a user system with permissions?
    private static final Path TOKEN_FILE = Paths.get("token.txt");
    private static final Registry registry = new Registry();

    private final List<Module> modules = new ArrayList<>();
    private final List<BeeBot> bots = new Vector<>();
    private String adminToken;
    private final ExecutorService botPool = Executors.newCachedThreadPool();

    public static Registry getInstance() {
        return registry;
    }

    private Registry() {
        init();
    }

    private void init() {
        initToken();
        initModules();
    }

    private void initToken() {
        log.info("Loading token");
        try {
            if (Files.exists(TOKEN_FILE)) {
                log.info("Loading admin token from file");
                this.adminToken = Files.readString(TOKEN_FILE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.adminToken == null) {
            this.adminToken = Util.generateToken();
            try {
                Files.writeString(TOKEN_FILE, this.adminToken);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("YOUR ADMIN TOKEN IS '" + this.adminToken + "'");
    }

    /**
     * Loads all subtypes of Module
     */
    private void initModules() {
        modules.clear();
        new Reflections("de.karlthebee.beebot.module").getSubTypesOf(Module.class).forEach(m -> {
            try {
                var module = m.newInstance();
                if (getModuleByShortName(module.getShortName()).isPresent()) {
                    log.error("Found two modules with same short name! '" + module.getShortName() + "' - second entry is ignored");
                    return;
                }
                modules.add(module);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Gets a module by its short name
     *
     * @param name the short name, see module definition
     * @return the module if available
     */
    public Optional<Module> getModuleByShortName(String name) {
        return this.modules.stream().filter(m -> m.getShortName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Get a BeeBot instance from uid
     * @param uid the uid, see beebot definition
     * @return the beebot if avaioable
     */
    public Optional<BeeBot> getBeeBotByUid(String uid) {
        return getBots().stream().filter(b -> b.getId().equalsIgnoreCase(uid)).findFirst();
    }

}
