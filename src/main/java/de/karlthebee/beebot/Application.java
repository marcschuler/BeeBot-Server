package de.karlthebee.beebot;

import de.karlthebee.beebot.repository.ConfigRepository;
import de.karlthebee.beebot.ts3.BeeBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class Application implements ApplicationRunner {
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void run(ApplicationArguments args) {
        Registry.getInstance();
        //Start all BeeBots
        configRepository.findAll()
                .parallelStream()
                .peek(c-> log.info("Starting BeeBot {}",c.getId()))
                .map(BeeBot::new)
                .peek(b -> beanFactory.autowireBean(b)) //autowire all beans
                .forEach(BeeBot::init);

        //CLose bots on exit
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            var bots = new ArrayList<>(Registry.getInstance().getBots()); // Copy list to avoid concurrent issues
            bots.forEach(BeeBot::close);
        }));
    }
}
