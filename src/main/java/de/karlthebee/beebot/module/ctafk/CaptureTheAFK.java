package de.karlthebee.beebot.module.ctafk;

import de.karlthebee.beebot.module.ctafk.worker.CaptureTheAFKWorker;
import de.karlthebee.beebot.module.modules.Module;
import de.karlthebee.beebot.module.modules.Worker;

public class CaptureTheAFK extends Module {
    @Override
    public Class<?> getConfigClass() {
        return CaptureTheAFKConfig.class;
    }

    @Override
    public Class<? extends Worker<?>> getWorker() {
        return CaptureTheAFKWorker.class;
    }

    @Override
    public String getName() {
        return "Capture The AFK";
    }

    @Override
    public String getShortName() {
        return "ctafk";
    }
}
