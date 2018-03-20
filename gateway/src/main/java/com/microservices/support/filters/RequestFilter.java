package com.microservices.support.filters;

import com.microservices.support.domain.User;
import com.microservices.support.repository.UserRepository;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

/**
 * Created by yichen.wei on 6/25/17.
 */
public class RequestFilter extends ZuulFilter {
    private static Logger log = LoggerFactory.getLogger(RequestFilter.class);

    @Autowired
    private UserRepository userRepository;

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

        if (princepal != null) {
            String username = request.getUserPrincipal().getName();
            log.info("Add username to X-REQUEST-UN->", username);
            ctx.addZuulRequestHeader("X-REQUESTED-UN", username);
        }

        // TODO add async for logging
        // TODO 1.Logging operation 2.Check permissions from cache
        User user = userRepository.findByUsernameCaseInsensitive(request.getUserPrincipal().getName());

//        ctx.addZuulRequestHeader("Authorization", request.getHeader("Authorization"));
        log.info(String.format("requestFilter... %s request to %s", request.getMethod(), request.getRequestURL().toString()));

        return null;
    }
}
