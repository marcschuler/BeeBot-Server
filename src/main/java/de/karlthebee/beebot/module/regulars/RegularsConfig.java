package de.karlthebee.beebot.module.regulars;

import de.karlthebee.beebot.dyn.WebValueDescriptor;
import de.karlthebee.beebot.dyn.WebValueDescriptorType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * A user gets serverGroup when he was at least x minutes online and is known since y days
 */
public class RegularsConfig {
    @Min(0)
    private int minutesOnline;
    @Min(0)
    private int daysOnline;

    @WebValueDescriptor(value = WebValueDescriptorType.SERVER_GROUP)
    private int serverGroup;
    @NotNull
    private String message;
}
