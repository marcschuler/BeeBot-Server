package de.karlthebee.beebot.ts3;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.reconnect.ConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import de.karlthebee.beebot.Util;
import de.karlthebee.beebot.Registry;
import de.karlthebee.beebot.data.ServerState;
import de.karlthebee.beebot.data.TeamspeakConfig;
import de.karlthebee.beebot.data.WorkerConfig;
import de.karlthebee.beebot.dyn.WebLog;
import de.karlthebee.beebot.module.Module;
import de.karlthebee.beebot.module.Worker;
import de.karlthebee.beebot.repository.WorkerConfigRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
@Slf4j
public class BeeBot implements TS3EventInterface {
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private TeamspeakConfig config;

    private TS3Query query;
    private TS3Api api;

    private final List<Worker<?>> workers = new ArrayList<>();
    private final WebLog webLog = new WebLog();

    private boolean closed = false;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;
    @Autowired
    private WorkerConfigRepository workerConfigRepository;

    public BeeBot(TeamspeakConfig config) {
        this.config = config;

    }

    /**
     * Initialises the BeeBot - should only be called once at start
     */
    public void init() {
        Registry.getInstance().getBots().add(this);
        System.out.println(config.toString());
        webLog.info("Starting BeeBot");
        initModule();
        Registry.getInstance().getBotPool().submit(this::initApi);
    }


    public String getId() {
        return getConfig().getId();
    }

    /**
     * Inits all modules
     */
    private void initModule() {
       webLog.info("Starting modules");
        log.info("Init modules...");

        for (var config : workerConfigRepository.findAll()) {
            if (config.getBotId().equals(getId())) {
                try {
                    addWorker(config);
                } catch (IllegalAccessException | InstantiationException e) {
                    webLog.error("Could not load module " + config.getModuleId());
                    log.error("Could not load module: " + config.toString(), e);
                }
            }
        }
        // .filter(p -> Registry.getInstance().getModuleByShortName(p.getParent().getFileName().toString()).isPresent()) //parent folder is a module
        // .peek(System.out::println)
        //  .collect(Collectors.toList());

        // for (var worker : workers) {
        //      var module = Registry.getInstance().getModuleByShortName(worker.getParent().getFileName().toString()).get();
        //      var config = Util.mapper().readValue(worker.toFile(), module.getConfigClass());
        //      addWorker(module, config, worker.getFileName().toString().replace(".config", ""));
        //   }
    }

    /**
     * Inits the API
     */
    private void initApi() {
        while (!isOnline() && !closed) {    //connect until is connected
            try {
                if (config == null)
                    return;
                log.info("Starting BeeBot " + getId() + " at " + getConfig().getHost());
                final TS3Config config = new TS3Config();
                config.setHost(getConfig().getHost());
                config.setCommandTimeout(4000);
                config.setReconnectStrategy(ReconnectStrategy.constantBackoff());
                config.setConnectionHandler(new ConnectionHandler() {
                    @Override
                    public void onConnect(TS3Query ts3Query) {
                        log.info("Reconnect: LOGIN");
                    }

                    @Override
                    public void onDisconnect(TS3Query ts3Query) {
                        log.info("Disconnected from server");
                        webLog.warning("Disconnected from teamspeak server");
                    }
                });

                query = new TS3Query(config);
                log.info("Connecting to server...");
                webLog.info("Connecting to Teamspeak Server");
                query.connect();
                var api = query.getApi();
                log.info("Login... " + getConfig().getUsername() + "@" + getConfig().getPassword());
              webLog.info("Loggin in with username '" + getConfig().getUsername() + "' and password");
                api.login(getConfig().getUsername(), getConfig().getPassword());
                api.selectVirtualServerById(getConfig().getVirtualServer());
                api.setNickname(getConfig().getNickname());

                api.registerAllEvents();
                api.addTS3Listeners(this);
                log.info("Connected");
               webLog.info("Connected");
                this.api = api;
                getWorkers().forEach(m -> m.onConnect(getApi()));
            } catch (Exception e) {
                log.error("Error connecting: " + e.getMessage());
                webLog.error("Error connecting: " + e.getMessage());
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Closes an BeeBot and all its modules
     */
    public void close() {
        Registry.getInstance().getBots().remove(this);  //remove access
        closed = true;
        getWorkers().forEach(Worker::save);                //Save worker (not needed, but...)
        getWorkers().forEach(Worker::stop);                //Stop worker
        try {
            query.exit();//Stop query at last so worker can use it
        } catch (Exception e) {
            log.error("Query exception: " + e.getMessage());
        }
        query = null;
        api = null;
    }

    /**
     * @return the current state
     */
    public ServerState state() {
        var state = new ServerState();
        state.setId(getId());
        state.setOnline(isOnline());
        state.setTeamspeakConfig(getConfig());

        return state;
    }

    /**
     * @return true if the bot is online (no guarantee, can change at any moment)
     */
    public boolean isOnline() {
        return query != null && api != null && query.isConnected();
    }

    /**
     * @return the api as optional
     */
    public Optional<TS3Api> withApi() {
        return Optional.ofNullable(getApi());
    }


    /**
     * Creates an new worker. Only "data" field has to be present,
     * the rest will be created
     *
     * @param module       the module to add
     * @param workerConfig the worker config
     * @param <T>          the config class
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <T> void createWorker(Module module, WorkerConfig<T> workerConfig) throws IllegalAccessException, InstantiationException {
        workerConfig.setBotId(getId());
        workerConfig.setModuleId(module.getShortName());
        workerConfig.setId(Util.randomId());
        workerConfigRepository.save(workerConfig);
        addWorker(workerConfig);
    }

    /**
     * Adds an (already existing) worker
     *
     * @param workerConfig the fully modelled workerconfig
     * @param <T>          The type of config
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <T> void addWorker(WorkerConfig<T> workerConfig) throws IllegalAccessException, InstantiationException {
        var module = Registry.getInstance().getModuleByShortName(workerConfig.getModuleId()).orElseThrow();
        @SuppressWarnings("unchecked")

        var worker = (Worker<T>) module.getWorker().newInstance();
        worker.setBot(this);
        worker.setStatus(true, "Started");
        worker.setWorkerConfig(workerConfig);
        beanFactory.autowireBean(worker);   //autowire


        log.info("Adding module " + module.getName() + "@" + worker.getId());
        getWorkers().add(worker);

        if (getApi() != null)
            worker.onConnect(getApi());

        worker.start();
    }

    public Optional<Worker<?>> workerById(String wid) {
        return getWorkers().stream()
                .filter(w -> w.getId().equals(wid))
                .findFirst();
    }


    @Override
    public void onTextMessage(TextMessageEvent e) {
        getApi().pokeClient(e.getInvokerId(), "BeeBot by karlthebee");
    }


}