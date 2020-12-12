package de.karlthebee.beebot.module.ctafk.worker;

import de.karlthebee.beebot.module.ctafk.data.CaptureTheAFKData;
import de.karlthebee.beebot.module.ctafk.data.CaptureTheAFKDay;
import org.apache.tomcat.jni.Local;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CaptureProcessorTest {

    private final LocalDateTime TODAY = LocalDateTime.now();
    private final LocalDateTime TODAY_MINUS_14 = TODAY.minusDays(14);

    @Test
    void getTimesForUsers() {

        var data = new CaptureTheAFKData();
        data.setDays(List.of(CaptureTheAFKDay.builder()
                .day(TODAY.toLocalDate()).afkdays(Map.of("uid1",128L,"uid2",64L)).build(),
                CaptureTheAFKDay.builder().
                        day(TODAY_MINUS_14.toLocalDate()).afkdays(Map.of("uid1",32L,"uid3",16L)).build()));

        var processedData = CaptureProcessor.getTimesForUsers(data, 1);
        Assert.assertEquals(Map.of("uid1",128L,"uid2",64L), processedData);

        processedData = CaptureProcessor.getTimesForUsers(data, 0);
        Assert.assertEquals(Map.of("uid1",128L,"uid2",64L), processedData);

        processedData = CaptureProcessor.getTimesForUsers(data,13);
        Assert.assertEquals(Map.of("uid1",128L,"uid2",64L), processedData);

        processedData = CaptureProcessor.getTimesForUsers(data,14);
        Assert.assertEquals(Map.of("uid1",128L+32L,"uid2",64L,"uid3",16L), processedData);

        processedData = CaptureProcessor.getTimesForUsers(data,15);
        Assert.assertEquals(Map.of("uid1",128L+32L,"uid2",64L,"uid3",16L), processedData);

        processedData = CaptureProcessor.getTimesForUsers(data,Integer.MAX_VALUE);
        Assert.assertEquals(Map.of("uid1",128L+32L,"uid2",64L,"uid3",16L), processedData);

        Assert.assertThrows(IllegalArgumentException.class,() -> {
            CaptureProcessor.getTimesForUsers(data,-1);
        });
    }

    @Test
    void getCaptureData() {
    }
}
