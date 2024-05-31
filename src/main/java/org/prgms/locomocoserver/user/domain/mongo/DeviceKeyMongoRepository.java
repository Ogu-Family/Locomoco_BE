package org.prgms.locomocoserver.user.domain.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DeviceKeyMongoRepository extends MongoRepository<DeviceKey, String> {
    Optional<DeviceKey> findByUserId(String userId);
}
