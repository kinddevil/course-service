package com.microservices.support.config;

import com.microservices.support.config.authFilters.RequestAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class OauthSecurityConfiguration {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Autowired
        private Environment env;

        @Value("${zuul.prefix}")
        private String apiPrefix;

        @Value("${zuul.whitelist}")
        private String whiteList;

        @Override
        public void configure(HttpSecurity http) throws Exception {

            String[] fullPathWhitelist = whiteList == null || whiteList.isEmpty() ?
                    new String[0] :
                        Arrays.stream(whiteList.split(","))
                                .map(s -> apiPrefix + s).toArray(String[]::new);
            http
                    .addFilterBefore(new RequestAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
//                    .antMatchers("/health_check", "/uua/login", "/uua/baseinfo", "/uua/oauth/token", "/uua/oauth/authorize", "/uua/oauth/confirm_access")
                    .antMatchers(fullPathWhitelist)
                    .permitAll()
                    .anyRequest().authenticated();

        }
    }

}

