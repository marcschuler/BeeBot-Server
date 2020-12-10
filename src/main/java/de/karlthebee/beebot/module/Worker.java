package de.karlthebee.beebot.module;

import com.github.theholywaffle.teamspeak3.TS3Api;
import de.karlthebee.beebot.Util;
import de.karlthebee.beebot.data.WorkerConfig;
import de.karlthebee.beebot.dyn.WebLog;
import de.karlthebee.beebot.repository.WorkerConfigRepository;
import de.karlthebee.beebot.ts3.BeeBot;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Objects;

/**
 * The Worker. One instance per activated module per bot
 * @param <T> the config class
 */
@Slf4j
public abstract class Worker<T> {

    @Autowired
    private WorkerConfigRepository workerConfigRepository;

    @Getter
    private WorkerConfig<T> workerConfig;
    //The Bot - guaranteed to be not null on runtime
    @Getter
    private BeeBot bot;


    @Getter
    protected final WebLog webLog = new WebLog();

    /**
     * @return the module. For convinient causes
     * TODO may be deprecated and done via Registry
     */
    public abstract Module getModule();

    /**
     * Sets the BeeBot instance. CAN ONLY BE SET ONCE
     * @param bot the bot instance
     * @throws IllegalStateException when the bot is already set
     */
    public void setBot(BeeBot bot) {
        if (this.bot != null)
            throw new IllegalStateException("BeeBot reference already set. No overwrite allowed");
        this.bot = Objects.requireNonNull(bot);
    }

    /**
     * Sets a worker config on initialise. ONLY CALL ONCE
     * @param workerConfig
     * @throws IllegalStateException
     * @throws NullPointerException
     */
    public void setWorkerConfig(WorkerConfig<T> workerConfig) throws IllegalStateException, NullPointerException {
        if (this.workerConfig != null)
            throw new IllegalStateException("Worker is already initialsed");
        if (workerConfig == null)
            throw new NullPointerException("Worker config is null");
        this.workerConfig = workerConfig;
    }

    public T getConfig() {
        return workerConfig.getData();
    }

    public String getId() {
        return workerConfig.getId();
    }

    /**
     * Gets called when a worker starts
     */
    abstract public void start();

    /**
     * Gets called when a worker is wanted to save its data
     */
    abstract public void save();

    /**
     * Sets the config without using generics
     * @param config the config
     * @throws ClassCastException when then config type didn't match the required ones
     */
    public void setConfigUnsafe(Object config) throws ClassCastException{
        try {
           setConfig((T) config);
        }catch(ClassCastException | IOException e){
            throw new IllegalStateException("Could not determine class type",e);
        }
    }

    /**
     * Sets the config after validation and saves it to database
     * @param config
     * @throws IOException
     */
    public void setConfig(T config) throws IOException {
        var errors = Util.validate(config);
        if (errors.size() > 0) {
            log.error("Could not set config");
            errors.forEach(e -> log.error(e.getRootBean() + ": " + e.getMessage()));
            webLog.error("Configuration is wrong");
            throw new IllegalStateException("Config is invalid");
        }
        this.workerConfig.setData(config);
        workerConfigRepository.save(getWorkerConfig());
        webLog.info("Configuration loaded");
        log.info("Updated config of worker " + getId());
    }

    /**
     * Gets called when the worker or server is shut down
     */
    abstract public void stop();

    /**
     * Gets called everytime the the bot get connected to the server
     * Used to setup data
     *
     * @param api the teamspeak api
     */
    abstract public void onConnect(TS3Api api);

    /**
     * Removes the worker
     */
    public void remove() {
        log.info("Deleting module " + getId() + "@" + getModule().getShortName());
        getBot().getWorkers().remove(this);
        try {
            save();
            stop();
        } catch (Exception e) {
            log.error("Could not stop bot. This is an inconsistence state", e);
        }
        workerConfigRepository.delete(getWorkerConfig());
    }

}
