package com.microservices.support.domain;

import lombok.*;

import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleDicts {
    // user - api post - api method
    // {admin: {user_post: rw_user, ...}} when switch is enabled
    Map<String, Map<String, PermissionInfo>> roles;
}

