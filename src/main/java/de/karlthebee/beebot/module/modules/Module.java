package de.karlthebee.beebot.module.modules;

public abstract class Module {

    /**
     * @return The class containing the configuration data
     */
    public abstract Class<?> getConfigClass();

    /**
     *
     * @return the worker class
     */
    public abstract Class<? extends Worker<?>> getWorker();

    /**
     * @return the name
     */
    public abstract String getName();

    /**
     *
     * @return the shortname. no spaces or special characters, as short as possible
     */
    public abstract String getShortName();

}
