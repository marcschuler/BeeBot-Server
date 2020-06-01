package de.karlthebee.beebot.rest;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("admin")
@CrossOrigin(origins = "*")
@Slf4j
public class AdminController {
    private static final Path DOCKER_PATH = Paths.get("/proc/1/cgroup");

    //Should NOT change at runtime...
    private static final String OS = System.getProperty("os.name");
    private static final boolean DOCKER;
    private static final String VERSION;

    static {
        boolean docker = false;
        if (Files.exists(DOCKER_PATH)) //-> Probably Linux
            try { //see https://stackoverflow.com/questions/52580008/how-does-java-application-know-it-is-running-within-a-docker-container/52581380
                docker = (Files.readString(DOCKER_PATH).contains("/docker"));
            } catch (IOException e) {
                log.warn("Could not open cgroup file", e);
            }

        DOCKER = docker;

        String version = System.getProperty("beebot.version");
        if (version == null) {
            log.warn("Could not load beebot version. Are you running ");
            version = "(testing)";
        }
        VERSION = version;
    }

    @GetMapping("stats")
    public ServerStats serverStats() {
        var serverStats = new ServerStats();
        var rt = Runtime.getRuntime();

        serverStats.setOs(OS);
        serverStats.setDocker(DOCKER);

        serverStats.setVersion(VERSION);

        serverStats.setMemoryTotal(rt.totalMemory());
        serverStats.setMemoryUsed(rt.totalMemory() - rt.freeMemory());
        return serverStats;
    }

    @Data
    static class ServerStats {
        private String os;
        private boolean docker;

        private String version;

        private long memoryUsed;
        private long memoryTotal;
    }
}
