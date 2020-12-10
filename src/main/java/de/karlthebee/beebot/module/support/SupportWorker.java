package de.karlthebee.beebot.module.support;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.karlthebee.beebot.Util;
import de.karlthebee.beebot.dyn.DynReplacer;
import de.karlthebee.beebot.module.Module;
import de.karlthebee.beebot.module.Worker;
import de.karlthebee.beebot.ts3.ApiUtil;
import de.karlthebee.beebot.ts3.TS3EventInterface;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class SupportWorker extends Worker<SupportConfig> implements TS3EventInterface {

    @Override
    public Module getModule() {
        return new Support();
    }

    @Override
    public void start() {
    }

    @Override
    public void save() {
        //nothing to save
    }

    @Override
    public void stop() {
        getBot().getApi().removeTS3Listeners(this);
    }

    @Override
    public void onConnect(TS3Api api) {
        api.addTS3Listeners(this);
        updateChannel();
        api.getClients().forEach(client -> notifyUser(client.getId(), client.getChannelId()));
    }

    @Override
    public void onClientJoin(ClientJoinEvent e) {
        updateChannel();
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
        updateChannel();
    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        int clientId = e.getClientId();
        int channelId = e.getTargetChannelId();
        notifyUser(clientId, channelId);
    }

    /**
     * Updates the channel description
     */
    public void updateChannel() {
        var channelOpt = ApiUtil.getChannelById(getBot().getApi(), getConfig().getChannelId());
        if (channelOpt.isEmpty()) {
            webLog.error("Support channel does not exist!");
            return;
        }
        var channel = channelOpt.get();
        var supporters = getSupporters();
        StringBuilder description = new StringBuilder(getConfig().getChannelDescription());

        description = new StringBuilder(DynReplacer.replaceAll(description.toString(), channel, null));

        // Add supporter to description
        if (getConfig().isShowSupporterInDescription()) {
            description.append("\n\n");
            for (var supporter : supporters) {
                description.append(ApiUtil.userReference(supporter.getId(), supporter.getUniqueIdentifier(), supporter.getNickname()));
                description.append("\n");
            }
        }

        //Ignore if the description is the same (saves one API call)
        var currentDescription = channel.getMap().get(ChannelProperty.CHANNEL_DESCRIPTION.toString());
        if (description.toString().equals(currentDescription))
            return;

        // update channel description
        getBot().getApi().editChannel(channel.getId(), Map.of(ChannelProperty.CHANNEL_DESCRIPTION, description.toString()));

    }

    /**
     * Notifies the client and the supporter when a client wants support
     *
     * @param clientId  the client
     * @param channelId the channel of the client
     */
    public void notifyUser(int clientId, int channelId) {
        //Check user and channel
        if (channelId != getConfig().getChannelId())
            return;
        var clientOpt = ApiUtil.getClientById(getBot().getApi(), clientId);
        if (clientOpt.isEmpty()) {
            log.warn("Could not find client {}", clientId);
            return;
        }

        //Prepare variables
        var client = clientOpt.get();
        var channel = ApiUtil.getChannelById(getBot().getApi(), channelId).orElseThrow(() -> {
            webLog.error("Could not find support channel by id '" + channelId + "'");
            throw new IllegalStateException("Could not find channel");
        });

        //Ignore supporters joining the support channel
        if (Util.contains(client.getServerGroups(), getConfig().getSupportGroup()))
            return;

        var customerMessage = getConfig().getCustomerMessage();
        var supporterMessage = getConfig().getSupporterMessage();

        customerMessage = DynReplacer.replaceAll(customerMessage, channel, client);
        supporterMessage = DynReplacer.replaceAll(supporterMessage, channel, client);

        try {
            // Find and send message to all supporters
            var supporters = getSupporters();
            for (var supporter : supporters) {
                if (!supporter.isServerQueryClient()) //ignore queries, can't poke anyway
                    ApiUtil.poke(getBot().getApi(), supporter.getId(), supporterMessage);
            }
        } catch (TS3CommandFailedException e) {
            log.warn("Could not poke admins", e);
            webLog.error("Could not poke admins: " + e.getMessage());
        }

        try {
            //Send message to user
            ApiUtil.poke(getBot().getApi(), clientId, customerMessage);
        } catch (TS3CommandFailedException e) {
            log.warn("Could not poke user", e);
            webLog.error("Could not poke user '" + client.getNickname() + "'");
        }
        webLog.info("User '" + client.getNickname() + "' wants help");
    }

    /**
     * @return a list of all online clients with support group
     */
    private List<Client> getSupporters() {
        return getBot().getApi().getClients().stream()
                .filter(c -> Util.contains(c.getServerGroups(), getConfig().getSupportGroup()))
                .collect(Collectors.toList());
    }
}
