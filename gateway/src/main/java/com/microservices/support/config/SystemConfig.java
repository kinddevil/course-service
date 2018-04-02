package com.microservices.support.config;

import com.microservices.support.domain.RAdmin;
import com.microservices.support.domain.RoleDicts;
import com.sun.org.apache.xpath.internal.operations.Bool;

public class SystemConfig {
    public static String R_ROLES_KEY = "allroles";

    public static String PERMISSION_SWITCH_KEY = "role::permission:enable";
    public static Boolean PERMISSION_SWITCH = false;

    public static RAdmin ROLES;

    public static String ROLE_DICTS_KEY = "role::permission:dict";
    public static RoleDicts ROLE_DICTS;
}
