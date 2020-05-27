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

    public static Map<String, Function<Channel, Object>> DYN_CHANNEL = Map.of(
            "channel_name", ChannelBase::getName,
            "channel_id", ChannelBase::getId

    );

    public static Map<String, Function<Client, Object>> DYN_CLIENT = Map.of(
            "client_name", Client::getNickname,
            "client_id", Client::getId,
            "client_ip", Client::getIp,
            "client_uid", Client::getUniqueIdentifier

    );

    public static Map<String, Function<String, Object>> DYN_STRING = Map.of(
            "\\n", (s) -> "\n",
            "\\", (s) -> "\t"
    );

    public static <T> String replaceAll(String s, Channel channel, Client client) {
        s = replace(s, channel, DYN_CHANNEL);
        s = replace(s, client, DYN_CLIENT);
        s = replace(s, "", DYN_STRING);

        return s;
    }

    public static <T> String replace(String s, T data, Map<String, Function<T, Object>> map) {
        if (data == null)
            return s;
        for (var entry : map.entrySet()) {
            s = s.replace("%"+entry.getKey()+"%", entry.getValue().apply(data).toString());
        }
        return s;
    }
}
