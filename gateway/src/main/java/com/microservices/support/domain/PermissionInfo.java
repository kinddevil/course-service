package com.microservices.support.domain;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PermissionInfo {
    String permisionKey;

    String permissionName;

    Boolean precise;

    String description;
}
