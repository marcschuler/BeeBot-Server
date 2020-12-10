package de.karlthebee.beebot.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class IpServiceTest {

    private IPService ipService = new IPService();

    @Test
    public void testFlags(){
        Assert.assertEquals(ipService.countryCodeToEmoji("us"),"\uD83C\uDDFA\uD83C\uDDF8");
        Assert.assertEquals(ipService.countryCodeToEmoji("de"),"\uD83C\uDDE9\uD83C\uDDEA");
        Assert.assertEquals(ipService.countryCodeToEmoji("DE"),"\uD83C\uDDE9\uD83C\uDDEA");
        System.out.println(ipService.countryCodeToEmoji("DE"));
    }
}
