package de.karlthebee.beebot.repository;

import de.karlthebee.beebot.data.WorkerConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface WorkerConfigRepository extends MongoRepository<WorkerConfig, UUID> {
}
