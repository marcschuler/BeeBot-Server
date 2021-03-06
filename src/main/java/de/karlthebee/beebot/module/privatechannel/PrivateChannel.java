package de.karlthebee.beebot.module.privatechannel;

import de.karlthebee.beebot.module.Module;
import de.karlthebee.beebot.module.Worker;

public class PrivateChannel extends Module {
    public Class<?> getConfigClass() {
        return PrivateChannelConfig.class;
    }

    public Class<? extends Worker<?>> getWorker() {
        return PrivateChannelWorker.class;
    }

    public String getName() {
        return "Private Channel";
    }

    @Override
    public String getShortName() {
        return "privatechannel";
    }

}
