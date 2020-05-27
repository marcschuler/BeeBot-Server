package de.karlthebee.beebot.module.privatechannel;

import de.karlthebee.beebot.dyn.WebValueDescriptor;
import de.karlthebee.beebot.dyn.WebValueDescriptorType;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
public class PrivateChannelConfig {
    @WebValueDescriptor(WebValueDescriptorType.CHANNEL)
    private int channelId;
    @WebValueDescriptor(WebValueDescriptorType.CHANNEL)
    private int parenChannel;
    @WebValueDescriptor(WebValueDescriptorType.CHANNEL_GROUP)
    @Min(1)
    private int channelGroup;
    @Size(min=3,max=64)
    private String channelName;
    private String channelDescription;
    @Min(0)
    private int deleteAfter = 30;

    private String message;
}
