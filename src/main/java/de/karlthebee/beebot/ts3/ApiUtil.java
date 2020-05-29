package de.karlthebee.beebot.ts3;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.List;
import java.util.Optional;

public class ApiUtil {

    /**
     * @param api      the api instance
     * @param clientId the client id
     * @return a client based on the id
     */
    public static Optional<Client> getClientById(TS3Api api, int clientId) {
        try {
            return api.getClients().stream().filter(c -> c.getId() == clientId).findFirst();
        } catch (TS3CommandFailedException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    /**
     * @param api       the api instance
     * @param channelId the channel id
     * @return the channel based on the id
     */
    public static Optional<Channel> getChannelById(TS3Api api, int channelId) {
        try {
            return api.getChannels().stream().filter(c -> c.getId() == channelId).findFirst();
        } catch (TS3CommandFailedException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    /**
     * Pokes a client securely.
     * Splits the text based on the 100-char limit and newline-limit of teamspeak
     *
     * @param api      the api instance
     * @param clientId the client id
     * @param text     the text
     */
    public static void poke(TS3Api api, int clientId, String text) throws TS3CommandFailedException {
        String[] pokes = text.split("\n");
        for (var poke : pokes) {
            var subpokes = split(poke);
            for (int n = 0; n < subpokes.length; ++n) {
                api.pokeClient(clientId, subpokes[n]);
            }
        }
    }

    /**
     * Splits a string in 100byte pieces
     *
     * @param s
     * @return
     */
    private static String[] split(String s) {
        if (s.length() < 100)
            return new String[]{s};
        var parts = (int) (Math.ceil(((float) s.length()) / 100));
        var array = new String[parts];
        for (int n = 0; n < parts; ++n) {
            array[n] = s.substring(n * 100, Math.min((n + 1) * 100, s.length()));
        }
        return array;
    }

    /**
     * Get the level of a channel. 0 for root, 1 for below...
     *
     * @param channel  the channel
     * @param channels all channels
     * @return the level of the channel. >=0
     */
    public static int getChannelLevel(Channel channel, List<Channel> channels) {
        int parent = channel.getParentChannelId();
        if (parent == -1)
            return 0;

        var parentChannel = channels.stream().filter(c -> c.getId() == parent)
                .findFirst();
        //?
        return parentChannel.map(value -> getChannelLevel(value, channels)).orElse(1);

    }


    /**
     * Generates a ts3-client parsable user link
     * Example: [URL=client://43/5Wwluvu55Nx51y4xjzsTg500ic0=~Bolkos]Bolkos[/URL]
     *
     * @param id   the user id
     * @param uid  the user uid
     * @param name the name (not nickname, just the visible name)
     * @return a string for a teamspeak message or description
     */
    public static String userReference(int id, String uid, String name) {
        return "[URL=client://" + id + "/" + uid + "~" + name + "]" + name + "[/URL]";
    }


}
