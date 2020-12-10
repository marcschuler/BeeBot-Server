package de.karlthebee.beebot.module.regulars;

import de.karlthebee.beebot.module.Module;
import de.karlthebee.beebot.module.Worker;

public class Regulars extends Module {
    @Override
    public Class<?> getConfigClass() {
        return RegularsConfig.class;
    }

    @Override
    public Class<? extends Worker<?>> getWorker() {
        return RegularsWorker.class;
    }

    @Override
    public String getName() {
        return "Regular Users";
    }

    @Override
    public String getShortName() {
        return "regulars";
    }
}
