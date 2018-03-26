package com.oauth.services.repository;

import com.oauth.services.domain.User;
import com.oauth.services.domain.UserAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAuditRepository extends JpaRepository<UserAudit, String> {

}