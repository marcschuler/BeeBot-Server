package de.karlthebee.beebot.module.support;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SupportConfig {
    @Min(0)
    private int channelId;
    @Min(0)
    private int supportGroup;
    @NotNull
    private String customerMessage = "A team member will soon talk to you";
    @NotNull
    private String supporterMessage = "%client_name% is requesting support";
    @NotNull
    private String channelDescription;
    private boolean showSupporterInDescription = true;
}
