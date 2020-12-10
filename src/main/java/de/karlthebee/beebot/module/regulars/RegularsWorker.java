package de.karlthebee.beebot.module.regulars;

import com.github.theholywaffle.teamspeak3.TS3Api;
import de.karlthebee.beebot.module.Module;
import de.karlthebee.beebot.module.Worker;

public class RegularsWorker extends Worker<RegularsConfig> {
    @Override
    public Module getModule() {
        return new Regulars();
    }

    @Override
    public void start() {

    }

    @Override
    public void save() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void onConnect(TS3Api api) {

    }
}
