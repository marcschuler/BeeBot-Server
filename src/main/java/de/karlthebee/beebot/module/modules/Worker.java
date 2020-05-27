package de.karlthebee.beebot.module.modules;

import com.github.theholywaffle.teamspeak3.TS3Api;
import de.karlthebee.beebot.Util;
import de.karlthebee.beebot.data.WorkerConfig;
import de.karlthebee.beebot.repository.WorkerConfigRepository;
import de.karlthebee.beebot.ts3.BeeBot;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;

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
    @Setter
    private BeeBot bot;

    /**
     * @return the module. For convinient causes
     * TODO may be deprecated and done via Registry
     */
    public abstract Module getModule();

    public void setWorkerConfig(WorkerConfig<T> workerConfig) throws IllegalStateException, NullPointerException {
        if (this.workerConfig != null)
            throw new IllegalStateException("Worker is already initialsed");
        if (workerConfig==null)
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
            setStatus(false, "Loaded wrong config");
            throw new IllegalStateException("Config is invalid");
        }
        this.workerConfig.setData(config);
        workerConfigRepository.save(getWorkerConfig());
        setStatus(true, "Loaded config");
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


    public void setStatus(boolean success, String text) {
        setStatus(new Status(success, text));
    }

    /**
     * Removes the worker
     */
    public void remove() {
        getBot().getWorkers().remove(this);
        save();
        stop();
        workerConfigRepository.delete(getWorkerConfig());
    }

}
