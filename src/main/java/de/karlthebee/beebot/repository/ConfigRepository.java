package de.karlthebee.beebot.repository;

import de.karlthebee.beebot.data.TeamspeakConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ConfigRepository extends MongoRepository<TeamspeakConfig, UUID> {
}
