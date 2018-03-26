package com.microservices.support.domain;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@RedisHash("role::admin")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RAdmin implements Serializable {
    private String id;
    private String name;
    private List roles;

    @TimeToLive
    private Long expiration;
}
