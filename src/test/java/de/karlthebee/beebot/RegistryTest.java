package de.karlthebee.beebot;

import org.junit.Assert;
import org.junit.Test;

public class RegistryTest {

    private final Registry registry = Registry.getInstance();

    @Test
    public void testRegistry() {
        Assert.assertTrue(registry.getBeeBotByUid("").isEmpty());
        Assert.assertEquals(0, registry.getBots().size());
        Assert.assertTrue(registry.getModules().size() > 0);
        Assert.assertEquals(registry.getModules().get(0), registry.getModuleByShortName(registry.getModules().get(0).getShortName()).get());
    }
}
