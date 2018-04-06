package com.microservices.support.filters;

import com.google.gson.Gson;
import com.microservices.support.config.SystemConfig;
import com.microservices.support.domain.*;
import com.microservices.support.repository.RUserPermissionRepository;
import com.microservices.support.repository.UserActionRepository;
import com.microservices.support.repository.UserRepository;
import com.microservices.support.workers.AsyncExecutor;
import com.microservices.support.workers.tasks.UserActionLogTask;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by yichen.wei on 6/25/17.
 */
public class RequestFilter extends ZuulFilter {
    private static Logger log = LoggerFactory.getLogger(RequestFilter.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AsyncExecutor asyncExecutor;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static Gson gson = new Gson();

    @Autowired
    private RUserPermissionRepository rUserPermissionRepository;

//    @Autowired
//    private UserActionLogTask userActionLogTask;

    // 300 seconds
    private static final Long CACHE_EXPIRE_TTL = 300L;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        // Map<String, List<String>> queryParams = new HashMap<>();
        String url = request.getRequestURL().toString();

        Principal principal = request.getUserPrincipal();

        Boolean needCheck = true;
        Boolean isGranted = false;
        String reason = "";
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        log.info(String.format("requestFilter... %s request to %s", request.getMethod(), request.getRequestURL().toString()));

        if (principal != null) {
            String username = request.getUserPrincipal().getName();
            log.info("Add username to X-REQUEST-UN->", username);
            ctx.addZuulRequestHeader("X-REQUESTED-UN", username);
        }

        // TODO remove test only function
//        refreshRedisKeys();

        if (principal != null && principal.getName() != null && SystemConfig.PERMISSION_SWITCH) {
            // Only admin will do this check
            User user = userRepository.findByUsernameCaseInsensitive(request.getUserPrincipal().getName());
            log.info(user.getPrivileges().toArray().toString());

            // Request like /api/advertising/v1/publicads, take "advertising-publicads"
            String method = request.getMethod();
            String api = request.getRequestURI();
            // Get api permission from request: eg - get_/api/users/v1/teacher
            String permissionKey = (method + "_" + api).toLowerCase();

            UserAction.UserActionBuilder userAction = UserAction.builder()
                    .userName(user.getUsername())
                    .name(user.getName())
                    .apiMethod(method)
                    .apiName(api)
                    .userType(user.getType())
                    .schoolID(user.getSchoolId())
                    .schoolName(user.getSchoolName())
                    .userType(user.getType())
                    .actionTime(currentTime);

            UserActionLogTask userActionLogTask = applicationContext.getBean(UserActionLogTask.class);

            // like school_admin
            if (SystemConfig.ROLE_WHITE_USER != null && SystemConfig.ROLE_WHITE_USER.contains(user.getUsername())
                    || SystemConfig.ROLE_WHITE_TYPE != null && SystemConfig.ROLE_WHITE_TYPE.contains(user.getType())
                    // api white list - get prefix
                    || SystemConfig.ROLE_WHITE_API != null && containsPrefix(SystemConfig.ROLE_WHITE_API, permissionKey)) {
                needCheck = false;
                isGranted = true;
                reason = "in white list";
                asyncExecutor.submit(userActionLogTask.BuildUser(
                    userAction
                        .message(reason)
                        .status(isGranted)
                        .build()
                ));
                return null;
            }

            // 0 is "" - /api/advertising/v1/publicads
//            String[] apiPath = api.split("/");
//            if (apiPath.length < 4) {
//                reason = "unknown api";
//                isGranted = false;
//                asyncExecutor.submit(userActionLogTask.BuildUser(
//                    userAction
//                        .message(reason)
//                        .status(isGranted)
//                        .build()
//                ));
//                setErrorResponse(ctx, reason);
//                return null;
//            }

            Map<String, PermissionInfo> typePermission = SystemConfig.ROLE_DICTS.getRoles().get(user.getType());
            if (typePermission == null) {
                reason = "unknown api with unknown user type";
                isGranted = false;
                asyncExecutor.submit(userActionLogTask.BuildUser(
                    userAction
                        .message(reason)
                        .status(isGranted)
                        .build()
                ));
                setErrorResponse(ctx, reason);
                return null;
            }
            // get by prefix
            PermissionInfo needPermission = getPermissionInfoByPrefix(typePermission, permissionKey);
            if (needPermission == null) {
                reason = "unknown api with unknown permission";
                isGranted = false;
                asyncExecutor.submit(userActionLogTask.BuildUser(
                    userAction
                        .message(reason)
                        .status(isGranted)
                        .build()
                ));
                setErrorResponse(ctx, reason);
                return null;
            }
            String permissionStr = needPermission.getPermisionKey();
            String permissionName = needPermission.getPermissionName();
            String permissionDes = needPermission.getDescription();

            // username_method_api
            String rPermissionKey = user.getUsername() + "_" + permissionKey;
            RUserPermission rUserPermission = rUserPermissionRepository.findOne(rPermissionKey);
            if (rUserPermission != null) {
                isGranted = rUserPermission.getPermission();
            } else {
                Set<Privilege> userRoles = user.getPrivileges();
                Set<String> userPermissions = new HashSet<String>();
                for (Privilege privilege : userRoles) {
                    String[] permissions = privilege.getPermissionIds().split(",");
                    for (String permit : permissions) {
                        if (permit.trim().equalsIgnoreCase(permissionStr)) { // has permission
                            isGranted = true;
                            break;
                        }
                    }
                }
                rUserPermissionRepository.save(
                    RUserPermission.builder()
                        .id(rPermissionKey)
                        .permission(isGranted)
                        .expiration(CACHE_EXPIRE_TTL)
                        .build()
                );
            }

            if (isGranted) {
                asyncExecutor.submit(userActionLogTask.BuildUser(
                    userAction
                        .message(reason)
                        .status(isGranted)
                        .actionName(permissionName)
                        .content(permissionDes)
                        .build()
                ));
            } else {
                reason = "user do not have permission";
                asyncExecutor.submit(userActionLogTask.BuildUser(
                    userAction
                        .message(reason)
                        .status(isGranted)
                        .actionName(permissionName)
                        .content(permissionDes)
                        .build()
                ));
                setErrorResponse(ctx, reason);
                return null;
            }

        } else {
            // Do not need log if switch is disabled or for anonymous user
        }

//        ctx.addZuulRequestHeader("Authorization", request.getHeader("Authorization"));

        return null;
    }

    private void setErrorResponse(RequestContext ctx, String errMsg) {
        log.warn(errMsg);
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(404);
        ctx.setResponseBody("{\"result\":\"" + errMsg +"\"}");
        ctx.getResponse().setContentType("text/html;charset=UTF-8");
    }

    // Test only
    private void refreshRedisKeys() {
        // Get permission enable switch
        String permissionsSwitch = stringRedisTemplate.opsForValue().get(SystemConfig.PERMISSION_SWITCH_KEY);
        SystemConfig.PERMISSION_SWITCH = Boolean.parseBoolean(permissionsSwitch);
        log.info("enable permission check...", SystemConfig.PERMISSION_SWITCH);

        // Get permission dicts
        String permissionsDict = stringRedisTemplate.opsForValue().get(SystemConfig.ROLE_DICTS_KEY);
        if (SystemConfig.PERMISSION_SWITCH && (permissionsDict == null || permissionsDict.isEmpty() ) ) {
            throw new ExceptionInInitializerError("Can not get permission data from redis");
        }

        SystemConfig.ROLE_DICTS = gson.fromJson(permissionsDict, RoleDicts.class);
        log.info("get permission dict from redis...", permissionsDict);

        // Get white list of user type
        String permissionsWhiteType = stringRedisTemplate.opsForValue().get(SystemConfig.ROLE_WHITE_TYPE_KEY);
        if (permissionsWhiteType != null && !permissionsWhiteType.equals("")) {
            SystemConfig.ROLE_WHITE_TYPE = Arrays.asList(permissionsWhiteType.split(","));
        }

        // Get white list of api
        String permissionsWhiteAPI = stringRedisTemplate.opsForValue().get(SystemConfig.ROLE_WHITE_API_KEY);
        if (permissionsWhiteAPI != null && !permissionsWhiteAPI.equals("")) {
            SystemConfig.ROLE_WHITE_API = Arrays.asList(permissionsWhiteAPI.split(","));
        }

        // Get white list of username
        String permissionsWhiteUser = stringRedisTemplate.opsForValue().get(SystemConfig.ROLE_WHITE_USER_KEY);
        if (permissionsWhiteUser != null && !permissionsWhiteUser.equals("")) {
            SystemConfig.ROLE_WHITE_USER = Arrays.asList(permissionsWhiteUser.split(","));
        }
    }

    private boolean containsPrefix(List<String> dicts, String target) {
        for (String dict : dicts) {
            if (dict != null && !dict.isEmpty() && target.startsWith(dict))
                return true;
        }
        return false;
    }

    private PermissionInfo getPermissionInfoByPrefix(Map<String, PermissionInfo> perMap, String api) {
        for (Map.Entry<String, PermissionInfo> entry : perMap.entrySet()) {
            String key = entry.getKey();
            PermissionInfo val = entry.getValue();
            Boolean precise = val.getPrecise();
            if (precise != null && precise) {
                if (key.equalsIgnoreCase(api))
                    return val;
            } else {
                if (api != null && !api.isEmpty() && api.startsWith(key))
                    return val;
            }
        }
        return null;
    }
}
