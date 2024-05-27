package org.prgms.locomocoserver.user.domain.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DeviceKeyMongoRepository extends MongoRepository<DeviceKeyMongo, String> {
    Optional<DeviceKeyMongo> findByUserId(String userId);
}
