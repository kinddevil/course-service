package com.oauth.services.controller;

import com.oauth.services.domain.Role;
import com.oauth.services.domain.User;
import com.oauth.services.repository.UserRepository;
import com.oauth.services.security.CustomAuthenticationEntryPoint;
import com.oauth.services.security.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

//    @Qualifier("oauth2ClientContext")
//    @Autowired
//    private OAuth2ClientContext oAuth2ClientContext;

//    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Principal getUserInfo(Principal currentUser, @RequestHeader(value="Authorization") String authorizationHeader) {
        log.info(String.format("auth header is -> %s", authorizationHeader));
        return currentUser;
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    @ResponseBody
    public User getUserDetail(Principal currentUser, @RequestHeader(value="Authorization") String authorizationHeader) {
        log.info(String.format("auth header is -> %s", authorizationHeader));
        String username = currentUser.getName();

//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

//        List<Role> roles = entityManager.createQuery(
//                "select r from Role r left join UserRole ur on r.name = ur.roleid where ur.userid = :username")
//                .getResultList();
//        log.info("roles...", roles.toString());

        User user = userRepository.findByUsernameCaseInsensitive(username);
        user.setPassword("");

        return user;
    }

}
