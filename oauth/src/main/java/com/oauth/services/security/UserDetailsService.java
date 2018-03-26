package com.oauth.services.security;

import com.oauth.services.domain.Authority;
import com.oauth.services.domain.User;
import com.oauth.services.domain.UserAudit;
import com.oauth.services.repository.UserAuditRepository;
import com.oauth.services.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;


@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuditRepository userAuditRepository;

    private enum AUDIT_TYPE {
        LOGIN,
        LOGOUT
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {

        log.debug("Authenticating {}", login);
        log.info("Authenticating... {}", login);
        String lowercaseLogin = login.toLowerCase();

        User userFromDatabase;
        if(lowercaseLogin.contains("@")) {
            userFromDatabase = userRepository.findByEmail(lowercaseLogin);
        } else {
            userFromDatabase = userRepository.findByUsernameCaseInsensitive(lowercaseLogin);
        }

        if (userFromDatabase == null) {
            throw new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database");
        } else if (!userFromDatabase.isActivated()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " is not activated");
        }

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority authority : userFromDatabase.getAuthorities()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getName());
            grantedAuthorities.add(grantedAuthority);
        }

        // login trigger
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        UserAudit userAudit = UserAudit.builder().userName(userFromDatabase.getUsername())
                .userType(userFromDatabase.getType())
                .schoolId(userFromDatabase.getSchoolId())
                .schoolName(userFromDatabase.getSchoolName())
                .actionTime(currentTime)
                .actionType(AUDIT_TYPE.LOGIN.toString())
                .build();
        userAuditRepository.save(userAudit);

        Integer currentCounter = userFromDatabase.getLoadCounter();
        userFromDatabase.setLoadCounter(currentCounter == null ? 1 : currentCounter + 1);
        userFromDatabase.setLastLoad(currentTime);
        userRepository.save(userFromDatabase);

        return new org.springframework.security.core.userdetails.User(userFromDatabase.getUsername(), userFromDatabase.getPassword(), grantedAuthorities);

    }

}
