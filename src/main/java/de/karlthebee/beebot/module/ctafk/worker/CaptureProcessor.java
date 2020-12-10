package de.karlthebee.beebot.module.ctafk.worker;

import de.karlthebee.beebot.module.ctafk.data.CaptureTheAFKData;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

/**
 * Calculated
 */
public class CaptureProcessor {

    /**
     * @param data the data
     * @param days the days to go back from the current day
     * @return a map of UIDs and times (in seconds) accumulated
     */
    public static Map<String, Long> getTimesForUsers(CaptureTheAFKData data, int days) {
        LocalDate today = LocalDate.now();
        LocalDate firstCollectedDay = today.minusDays(days);

        Map<String, Long> userMap = new HashMap<>();
        for (var day : data.getDays()) {
            for (var entry : day.getAfkdays().entrySet()) {
                var user = entry.getKey();
                var time = entry.getValue();
                userMap.putIfAbsent(user, 0L);
                userMap.put(user, userMap.get(user) + time);
            }
        }
        return userMap;
    }

    /**
     *
     * @param data the afk data
     * @param days the last days (see getTimesForUsers(...))
     * @return a list of all capturedata, sorted by time
     */
    public static List<CaptureData> getCaptureData(CaptureTheAFKData data, int days) {
        var timesMap = getTimesForUsers(data, days);

        var timesSum = timesMap.values().stream().mapToLong(v -> v).sum();

        var captureData = new ArrayList<CaptureData>();

        for (var entry : timesMap.entrySet()) {
            var capture = new CaptureData();
            capture.uid = entry.getKey();
            capture.time = entry.getValue();
            capture.percentage = (float) capture.time / timesSum;
            capture.nickname = data.getKnownNames().getOrDefault(capture.uid,capture.uid);
        }

        Collections.sort(captureData);

        return captureData;
    }


    @Data
    static
    class CaptureData implements Comparable<CaptureData>{
        private String nickname;
        private String uid;
        private long time;
        private double percentage;


        @Override
        public int compareTo(CaptureData captureData) {
            return Long.compare(getTime(),captureData.getTime());
        }
    }

}
