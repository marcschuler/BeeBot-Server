package de.karlthebee.beebot.module.support;

import de.karlthebee.beebot.module.Module;
import de.karlthebee.beebot.module.Worker;

public class Support extends Module {
    @Override
    public Class<?> getConfigClass() {
        return SupportConfig.class;
    }

    @Override
    public Class<? extends Worker<?>> getWorker() {
        return SupportWorker.class;
    }

    @Override
    public String getName() {
        return "Support";
    }

    @Override
    public String getShortName() {
        return "support";
    }
}
