package de.karlthebee.beebot.module.ctafk.data;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CaptureTheAFKDataRepository extends MongoRepository<CaptureTheAFKData,String> {
}
