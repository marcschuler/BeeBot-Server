package de.karlthebee.beebot.module.ctafk;

import de.karlthebee.beebot.dyn.WebValueDescriptor;
import de.karlthebee.beebot.dyn.WebValueDescriptorType;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class CaptureTheAFKConfig {

    @WebValueDescriptor(value = WebValueDescriptorType.CHANNEL)
    private int channelId;
    private String channelName = "%client_name% is the winner!";
    private String channelDescription = "";
    @Min(0)
    private int showLastDays = 14;
    @Min(1)
    private int channelUpdateMins = 5;
}
