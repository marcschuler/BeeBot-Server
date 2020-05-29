package de.karlthebee.beebot.module.privatechannel;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;
import de.karlthebee.beebot.dyn.DynReplacer;
import de.karlthebee.beebot.module.Module;
import de.karlthebee.beebot.module.Worker;
import de.karlthebee.beebot.ts3.ApiUtil;
import de.karlthebee.beebot.ts3.TS3EventInterface;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PrivateChannelWorker extends Worker<PrivateChannelConfig> implements TS3EventInterface {

    @Override
    public Module getModule() {
        return new PrivateChannel();
    }

    @Override
    public void start() {
        if (getBot().isOnline())
            getBot().getApi().getClients().forEach(client -> createChannel(client.getId(), client.getChannelId()));
    }

    @Override
    public void save() {
        //nothing to save
    }

    @Override
    public void stop() {
        getBot().withApi().ifPresent(ts3Api -> ts3Api.removeTS3Listeners(this));
    }

    @Override
    public void onConnect(TS3Api api) {
        api.addTS3Listeners(this);
        api.getClients().forEach(client -> createChannel(client.getId(), client.getChannelId()));
    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        int clientId = e.getClientId();
        int channelId = e.getTargetChannelId();
        createChannel(clientId, channelId);
    }


    public void createChannel(int clientId, int channelId) {
        if (channelId != getConfig().getChannelId())
            return;
        var clientOpt = ApiUtil.getClientById(getBot().getApi(), clientId);
        if (clientOpt.isEmpty()) {
            log.warn("Could not find client {}", clientId);
            return;
        }
        var client = clientOpt.get();
        log.info("Creating private channel for {}", client.getNickname());
        webLog.info("Creating private channel for '" + client.getNickname() + "'");

        var name = getConfig().getChannelName();
        var description = getConfig().getChannelDescription();

        name = DynReplacer.replaceAll(name, null, client);
        description = DynReplacer.replaceAll(description, null, client);

        final Map<ChannelProperty, String> properties = new HashMap<>();
        properties.put(ChannelProperty.CPID, String.valueOf(getConfig().getParenChannel()));

        if (getConfig().getDeleteAfter() > 0) {
            properties.put(ChannelProperty.CHANNEL_FLAG_TEMPORARY, "1");
            properties.put(ChannelProperty.CHANNEL_DELETE_DELAY, String.valueOf(getConfig().getDeleteAfter()));
        } else {
            properties.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "1");
        }

        properties.put(ChannelProperty.CHANNEL_DESCRIPTION, description);

        try {
            ServerQueryInfo whoAmI = getBot().getApi().whoAmI();
            int newChannel = 0;
            try {
                newChannel = getBot().getApi().createChannel(name, properties);
            } catch (TS3CommandFailedException e) {
                log.warn("Could not create channel", e);
                webLog.warning("Could not create channel. Doesn't BeeBot have permission or does the channel already exist? " + e.getMessage());
                return;
            }
            getBot().getApi().moveClient(clientId, newChannel);

            //Move with non permanent channel
            if (getConfig().getDeleteAfter() == 0)
                getBot().getApi().moveClient(whoAmI.getId(), whoAmI.getChannelId());
            getBot().getApi().setClientChannelGroup(getConfig().getChannelGroup(), newChannel, client.getDatabaseId());
            webLog.info("Private Channel '" + name + "' created");

            if (!getConfig().getMessage().equals(""))
                ApiUtil.poke(getBot().getApi(), clientId, getConfig().getMessage());
        } catch (Exception e) {
            log.error("Could not create channel", e);
            webLog.error("Could not create channel: " + e.getMessage());
        }
    }
}
