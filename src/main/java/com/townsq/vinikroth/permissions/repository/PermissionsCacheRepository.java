package com.townsq.vinikroth.permissions.repository;

import com.townsq.vinikroth.permissions.entity.UserCacheDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionsCacheRepository extends CrudRepository<UserCacheDocument, String> {
}