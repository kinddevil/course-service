package com.api.test;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;

/**
 * Created by yichen.wei on 6/25/17.
 */
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
//@EnableOAuth2Resource
//@EnableOAuth2Sso
public class APITestApplication {
    public static void main(String[] args) {
        SpringApplication.run(APITestApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}


@RestController
@RequestMapping("/{userId}/bookmarks")
class BookmarkRestController {

    @Autowired
    private BookService bookService;

    @RequestMapping(method = RequestMethod.GET)
    String getBookmarks(@PathVariable String userId) {
        return "user1";
    }

    @RequestMapping(value = "/{bookmarkId}", method = RequestMethod.GET)
    String getBookmark(@PathVariable String userId,
                         @PathVariable Long bookmarkId) {
        return "bookmark1";
    }

    @RequestMapping(value = "/curcuit", method = RequestMethod.GET)
    String defaultBookmark(@PathVariable String userId, Principal principal) {
        return bookService.getBookmarkCurcuit();
    }

    public String reliable() {
        return "Fallback -> Cloud Native Java (O'Reilly)";
    }
}

@RestController
@RequestMapping("/circuit")
class CircuitBookmarkRestController {

    @RequestMapping(method = RequestMethod.GET)
    String getBookmarks(Principal principal, OAuth2Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getUserAuthentication().getAuthorities();
//        return "circuit1" + principal.getName();
        StringBuilder s = new StringBuilder();
        for (GrantedAuthority a : authorities) {
            s.append(a.getAuthority()).append(" ");
        }
        return s.toString();
    }

}

// HystrixCommand currently only works in a class marked with @Component or @Service
@Service
class BookService {

    private final RestTemplate restTemplate;

    public BookService(RestTemplate rest) {
        this.restTemplate = rest;
    }

    @HystrixCommand(fallbackMethod = "reliable")
    public String getBookmarkCurcuit() {
        URI uri = URI.create("http://localhost:7070/test/circuit"); // Gateway
        return this.restTemplate.getForObject(uri, String.class);
    }

    public String reliable() {
        return "fallback -> break";
    }

}