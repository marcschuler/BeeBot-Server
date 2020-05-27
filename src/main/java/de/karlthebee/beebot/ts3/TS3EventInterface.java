package de.karlthebee.beebot.ts3;

import com.github.theholywaffle.teamspeak3.api.event.*;

/**
 * Interface which implements all methods by default.
 */
public interface TS3EventInterface extends TS3Listener {

  
    default void onTextMessage(TextMessageEvent e) {}


    default void onClientJoin(ClientJoinEvent e) {}

  
    default void onClientLeave(ClientLeaveEvent e) {}

  
    default void onServerEdit(ServerEditedEvent e) {}

  
    default void onChannelEdit(ChannelEditedEvent e) {}

  
    default void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {}

  
    default void onClientMoved(ClientMovedEvent e) {}

  
    default void onChannelCreate(ChannelCreateEvent e) {}

  
    default void onChannelDeleted(ChannelDeletedEvent e) {}

  
    default void onChannelMoved(ChannelMovedEvent e) {}

  
    default void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {}

  
    default void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent e) {}
}
