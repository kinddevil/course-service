package com.microservices.support.repository;

import com.microservices.support.domain.User;
import com.microservices.support.domain.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserActionRepository extends JpaRepository<UserAction, String> {

}