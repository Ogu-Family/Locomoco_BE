package org.prgms.locomocoserver.user.domain.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceKeyMongoRepository extends MongoRepository<DeviceKeyMongo, String> {
    DeviceKeyMongo findByUserId(String userId);
}
