package com.microservices.support.filters;

import com.microservices.support.config.SystemConfig;
import com.microservices.support.domain.PermissionInfo;
import com.microservices.support.domain.Privilege;
import com.microservices.support.domain.User;
import com.microservices.support.repository.UserRepository;
import com.microservices.support.workers.AsyncExecutor;
import com.microservices.support.workers.tasks.UserActionLogTask;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Created by yichen.wei on 6/25/17.
 */
public class RequestFilter extends ZuulFilter {
    private static Logger log = LoggerFactory.getLogger(RequestFilter.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AsyncExecutor asyncExecutor;

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

        Principal princepal = request.getUserPrincipal();

        Boolean needCheck = true;
        Boolean isGranted = false;

        log.info(String.format("requestFilter... %s request to %s", request.getMethod(), request.getRequestURL().toString()));

        if (princepal != null) {
            String username = request.getUserPrincipal().getName();
            log.info("Add username to X-REQUEST-UN->", username);
            ctx.addZuulRequestHeader("X-REQUESTED-UN", username);
        }

        // TODO add async for logging
        // TODO 1.Logging operation 2.Check permissions from cache
        if (request.getUserPrincipal().getName() != null && SystemConfig.PERMISSION_SWITCH) {
            // Only admin will do this check
            User user = userRepository.findByUsernameCaseInsensitive(request.getUserPrincipal().getName());
            log.info(user.getPrivileges().toArray().toString());

            // Request like /api/advertising/v1/publicads, take "advertising-publicads"
            String method = request.getMethod();
            String api = request.getRequestURI();

            // school_admin has all permissions
            if (user.getType() == "super" || user.getType() == "school_admin" || api.contains("pwsrst")) { //reset password
                asyncExecutor.submit(new UserActionLogTask());
                needCheck = false;
                return null;
            }

            String[] apiPath = api.split("/");
            if (apiPath.length < 4) {
                asyncExecutor.submit(new UserActionLogTask());
                String errMsg = "unknown api";
                log.warn(errMsg);
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(404);
                ctx.setResponseBody("{\"result\":\"" + errMsg +"\"}");
                ctx.getResponse().setContentType("text/html;charset=UTF-8");
                return null;
            }

            // Get api permission from request
            String permissionKey = (method + "_" + apiPath[2]).toLowerCase();
            Map<String, PermissionInfo> typePermission = SystemConfig.ROLE_DICTS.getRoles().get(user.getType());
            if (typePermission == null) {
                asyncExecutor.submit(new UserActionLogTask());
                String errMsg = "unknown api with unknown user type";
                log.warn(errMsg);
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(401);
                ctx.setResponseBody("{\"result\":\"" + errMsg +"\"}");
                ctx.getResponse().setContentType("text/html;charset=UTF-8");
                return null;
            }
            PermissionInfo needPermission = typePermission.get(permissionKey);
            if (needPermission == null) {
                asyncExecutor.submit(new UserActionLogTask());
                String errMsg = "unknown api with unknown permission";
                log.warn(errMsg);
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(401);
                ctx.setResponseBody("{\"result\":\"" + errMsg +"\"}");
                ctx.getResponse().setContentType("text/html;charset=UTF-8");
                return null;
            }
            String permissinStr = needPermission.getPermisionKey();

            Set<Privilege> userRoles = user.getPrivileges();

            Set<String> userPermissions = new HashSet<String>();
            for (Privilege privilege : userRoles) {
                String[] permissions = privilege.getPermissionIds().split(",");
                for (String permit : permissions) {
                    if (permit.trim().equalsIgnoreCase(permissinStr)) { // has permission
                        isGranted = true;
                        break;
                    }
                }
            }

            if (isGranted) {
                asyncExecutor.submit(new UserActionLogTask());
            } else {
                asyncExecutor.submit(new UserActionLogTask());
                String errMsg = "user do not have permission";
                log.warn(errMsg);
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(401);
                ctx.setResponseBody("{\"result\":\"" + errMsg +"\"}");
                ctx.getResponse().setContentType("text/html;charset=UTF-8");
                return null;
            }

        }

//        ctx.addZuulRequestHeader("Authorization", request.getHeader("Authorization"));

        return null;
    }
}
