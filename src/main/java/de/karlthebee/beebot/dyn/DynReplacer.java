package de.karlthebee.beebot.dyn;

import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelBase;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.Map;
import java.util.function.Function;

/**
 * Replaces %names% with actual names
 */
public class DynReplacer {

    //Changes channel data
    public static Map<String, Function<Channel, Object>> DYN_CHANNEL = Map.of(
            "channel_name", ChannelBase::getName,
            "channel_id", ChannelBase::getId

    );

    //Changes client data
    public static Map<String, Function<Client, Object>> DYN_CLIENT = Map.of(
            "client_name", Client::getNickname,
            "client_id", Client::getId,
            "client_ip", Client::getIp,
            "client_uid", Client::getUniqueIdentifier

    );

    //Changes general data
    public static Map<String, Function<String, Object>> DYN_STRING = Map.of(
            "\\n", (s) -> "\n",
            "\\", (s) -> "\t"
    );

    /**Replces DYN_STRINGS, channel and client in one step - convenient function
     *
     * @param s the input
     * @param channel the channel
     * @param client the client
     * @return the formatted string
     */
    public static String replaceAll(String s, Channel channel, Client client) {
        s = replace(s, channel, DYN_CHANNEL);
        s = replace(s, client, DYN_CLIENT);
        s = replace(s, "", DYN_STRING);

        return s;
    }

    /**
     *
     * @param s input string
     * @param data the data to transform
     * @param map a map converting the strings with T to data
     * @param <T> the type of input
     * @return the formatted string
     */
    public static <T> String replace(String s, T data, Map<String, Function<T, Object>> map) {
        if (s==null || data == null)
            return s;
        for (var entry : map.entrySet()) {
            s = s.replace("%"+entry.getKey()+"%", entry.getValue().apply(data).toString());
        }
        return s;
    }
}
