package com.microservices.support.repository;

import com.microservices.support.domain.RUserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface RUserPermissionRepository extends CrudRepository<RUserPermission, String> {

}