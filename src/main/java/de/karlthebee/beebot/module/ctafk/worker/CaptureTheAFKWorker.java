package de.karlthebee.beebot.module.ctafk.worker;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.karlthebee.beebot.module.ctafk.CaptureTheAFK;
import de.karlthebee.beebot.module.ctafk.CaptureTheAFKConfig;
import de.karlthebee.beebot.module.ctafk.data.CaptureTheAFKData;
import de.karlthebee.beebot.module.ctafk.data.CaptureTheAFKDataRepository;
import de.karlthebee.beebot.module.ctafk.data.CaptureTheAFKDay;
import de.karlthebee.beebot.module.Worker;
import de.karlthebee.beebot.module.Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * CTAFK worker
 */
@Slf4j
public class CaptureTheAFKWorker extends Worker<CaptureTheAFKConfig> {

    @Autowired
    private CaptureTheAFKDataRepository captureTheAFKDataRepository;

    //Use scheduler to calculate and set data on a regular basis
    private ScheduledFuture<?> future;
    private ScheduledFuture<?> futureDescription;

    private CaptureTheAFKData data;

    @Override
    public Module getModule() {
        return new CaptureTheAFK();
    }

    @Override
    public void start() {
        log.info("Starting scheduler");
        this.data = captureTheAFKDataRepository.findById(getId())
                .orElse(new CaptureTheAFKData());

        if (future != null)
            log.warn("Future is already active. start() is called twice!");
        future = getBot().getScheduler().scheduleWithFixedDelay(() -> {
            try {
                update();
            } catch (Exception e) {
                log.error("Could not update", e);
            }
        }, 60, 60, TimeUnit.SECONDS);
        futureDescription = getBot().getScheduler().scheduleWithFixedDelay(() -> {
            try {
                updateChannel();
            } catch (Exception e) {
                log.error("Could not update channel", e);
            }
        }, 1, getConfig().getChannelUpdateMins(), TimeUnit.MINUTES);
    }

    @Override
    public void save() {
        captureTheAFKDataRepository.save(data);
    }

    @Override
    public void stop() {
        if (future != null) {
            log.info("Stopping scheduler");
            future.cancel(true);
            future = null;
        } else {
            log.warn("Stopping without future");
        }
    }

    @Override
    public void remove() {
        if (data != null && captureTheAFKDataRepository.existsById(getId())) {
            log.info("Removing CTAFK data");
            captureTheAFKDataRepository.delete(data);   //Remove data
        }else{
            log.info("No CTAFK data to remove");
        }
        super.remove();
    }

    @Override
    public void onConnect(TS3Api api) {

    }

    /**
     * Updates the channel
     */
    private void updateChannel() {
        if (!getBot().isOnline()) //bot must be online
            return;

        StringBuilder description = new StringBuilder();
        if (getConfig().getChannelDescription() != null)
            description.append(getConfig().getChannelDescription());

        var alltimeData = CaptureProcessor.getCaptureData(data, 365 * 10);

        description.append("\n");
        for (int n = 0; n < Math.min(25, alltimeData.size()); n++) {
            var data = alltimeData.get(n);
            description.append("# ").append(n + 1).append(" ");
            description.append(data.getNickname());
            description.append(" ");
            description.append(data.getTime() / (60 * 60)).append("h ").append(data.getTime() / 60 % 60).append("min");
            description.append("\n");
        }

        try {
            getBot().getApi().editChannel(getConfig().getChannelId(), ChannelProperty.CHANNEL_DESCRIPTION, description.toString());
        } catch (TS3CommandFailedException e) {
            e.printStackTrace();
            getWebLog().error("Could not update channel "+getConfig().getChannelId()+" description: " + e.getMessage());
        }
    }

    /**
     * Minutly update
     */
    private void update() {
        if (!getBot().isOnline())
            return;

        List<Client> clients;
        try {
            clients = getBot().getApi().getClients();
        } catch (TS3CommandFailedException e) {
            log.warn("Could not fetch client list. Is the server offline?", e);
            return;
        }

        var ctafkClients = new ArrayList<String>();

        for (var client : clients) {
            if (client.getChannelId() == getConfig().getChannelId()) {
                ctafkClients.add(client.getNickname());
                addClient(client);
            }
        }

        log.info("CTAFK online clients: " + String.join(", ", ctafkClients));
        save();
    }

    /**
     * Adds a minute to the client
     *
     * @param client the client
     */
    private void addClient(Client client) {
        var today = LocalDate.now();
        //get day
        var dataDayOpt = data.getDays().stream().filter(day -> day.getDay().isEqual(today)).findFirst();
        CaptureTheAFKDay dataDay;
        if (dataDayOpt.isEmpty()) {
            dataDay = new CaptureTheAFKDay();
            dataDay.setDay(today);
            data.getDays().add(dataDay);
        } else {
            dataDay = dataDayOpt.get();
        }
        var uid = client.getUniqueIdentifier();
        dataDay.getAfkdays().putIfAbsent(uid, 0L);  //create entry
        dataDay.getAfkdays().put(uid, dataDay.getAfkdays().get(uid) + 60); //add 60 seconds
        //set latest nickname
        data.getKnownNames().put(uid, client.getNickname());
    }
}
