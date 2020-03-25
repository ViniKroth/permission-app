package com.townsq.vinikroth.permissions.repository;

import com.townsq.vinikroth.permissions.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionsRepository extends MongoRepository<User, String> {
}
