package de.karlthebee.beebot.module.support;

import de.karlthebee.beebot.module.modules.Module;
import de.karlthebee.beebot.module.modules.Worker;

public class SupportModule extends Module {
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
