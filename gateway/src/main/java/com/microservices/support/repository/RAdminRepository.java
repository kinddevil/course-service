package com.microservices.support.repository;

import com.microservices.support.domain.RAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface RAdminRepository extends CrudRepository<RAdmin, String> {

}