package com.microservices.support.domain;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

@RedisHash("role::permission")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RUserPermission implements Serializable {
    // username + permission: like aaa_post_users
    private String id;
    private Boolean permission;

    @TimeToLive
    private Long expiration;
}
