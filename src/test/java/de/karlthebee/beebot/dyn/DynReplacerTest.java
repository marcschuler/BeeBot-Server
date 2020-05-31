package de.karlthebee.beebot.dyn;

import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class DynReplacerTest {
    private final Channel testChannel = new Channel(
            Map.of(
                    "cid","1",
                    "channel_name","Test Channel"
            ));

    @Test
    public void testNoReplace(){
        Assert.assertEquals("no string",DynReplacer.replaceAll("no string",null,null));
        Assert.assertNull(DynReplacer.replaceAll(null, null, null));
        Assert.assertEquals("",DynReplacer.replaceAll("", null, null));
        Assert.assertEquals("null",DynReplacer.replaceAll("null", null, null));
    }

    @Test
    public void testSimpleReplace(){
        Assert.assertEquals("You are in channel 'Test Channel'",
                DynReplacer.replaceAll("You are in channel '%channel_name%'",testChannel,null));
    }
}
