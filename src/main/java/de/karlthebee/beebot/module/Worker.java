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

@Slf4j
public abstract class Worker<T> {

    @Autowired
    private WorkerConfigRepository workerConfigRepository;

    @Getter
    private WorkerConfig<T> workerConfig;
    @Getter
    @Setter
    private Status status;
    @Getter
    private BeeBot bot;


    @Getter
    protected final WebLog webLog = new WebLog();

    /**
     * @return the module. For convinient causes
     * TODO may be deprecated and done via Registry
     */
    public abstract Module getModule();

    public void setBeeBot(BeeBot bot) {
        if (this.bot != null)
            throw new IllegalStateException("BeeBot reference already set. No overwrite allowed");
        this.bot = bot;
    }

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
