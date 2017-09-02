package com.oauth.services.controller;

import com.oauth.services.domain.User;
import com.oauth.services.security.CustomAuthenticationEntryPoint;
import com.oauth.services.security.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDetailsService userDetailsService;

//    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Principal getUserInfo(Principal currentUser, @RequestHeader(value="Authorization") String authorizationHeader) {
        log.info(String.format("auth header is -> %s", authorizationHeader));
        return currentUser;
    }

}
