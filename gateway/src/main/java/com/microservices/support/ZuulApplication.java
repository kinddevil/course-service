package com.microservices.support;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microservices.support.config.SystemConfig;
import com.microservices.support.domain.PermissionInfo;
import com.microservices.support.domain.RAdmin;
import com.microservices.support.domain.RoleDicts;
import com.microservices.support.filters.ErrorFilter;
import com.microservices.support.filters.RequestFilter;
import com.microservices.support.filters.ResponseFilter;
import com.microservices.support.filters.RouteFilter;
import com.microservices.support.repository.RAdminRepository;
import com.microservices.support.workers.AsyncExecutor;
import com.microservices.support.workers.AsyncTask;
import com.microservices.support.workers.tasks.UserActionLogTask;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@Controller
@EnableZuulProxy
@RestController
@EnableDiscoveryClient
@EnableFeignClients
//@EnableOAuth2Sso
//public class ZuulApplication extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer, ErrorController { // For 1.5.x
public class ZuulApplication extends SpringBootServletInitializer implements WebServerFactoryCustomizer, ErrorController {
	
    public static void main(String[] args) {
        new SpringApplicationBuilder(ZuulApplication.class).web(true).run(args);
    }

//    @Autowired
//    private RAdminRepository rAdminRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static Gson gson = new Gson();

    private static final Logger LOGGER = LoggerFactory.getLogger(ZuulApplication.class);

    @Override
    public void customize(WebServerFactory factory) {

    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();

//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("*");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("OPTIONS");
//        config.addAllowedMethod("HEAD");
//        config.addAllowedMethod("GET");
//        config.addAllowedMethod("PUT");
//        config.addAllowedMethod("POST");
//        config.addAllowedMethod("DELETE");
//        config.addAllowedMethod("PATCH");
//        source.registerCorsConfiguration("/**", config);

        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @LoadBalanced
    @Bean
    public RequestFilter requestFilter() {
        return new RequestFilter();
    }

    @LoadBalanced
    @Bean
    public ResponseFilter responseFilter() {return new ResponseFilter();}

    @LoadBalanced
    @Bean
    public RouteFilter routeFilter() {
        return new RouteFilter(new ProxyRequestHelper(), new ZuulProperties());
    }

    @LoadBalanced
    @Bean
    public ErrorFilter errorFilter() {
        return new ErrorFilter();
    }

    @Bean
    public AsyncExecutor asyncExecutor() {
        return new AsyncExecutor();
    }

    @RequestMapping(value = "/health_check")
    public String healthCheck() {
//        stringRedisTemplate.opsForValue().set("", "");
        return "ok";
    }

    @Override
    public String getErrorPath() {
        return "/health_check";
    }

    @Bean
    InitializingBean setRedisData() throws FactoryBeanNotInitializedException {

//        Templdate redis
//        Map admin = Collections.unmodifiableMap(Stream.of(
//                new AbstractMap.SimpleEntry<>("school_r", "school read"),
//                new AbstractMap.SimpleEntry<>("school_rw", "school write"))
//                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
//        Map roles = Collections.unmodifiableMap(Stream.of(
//                new AbstractMap.SimpleEntry<>("admin", admin))
//                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
//        List roleList = Arrays.asList("a=1", "b=2");
//        RAdmin rAdmin = RAdmin.builder().id(SystemConfig.R_ROLES_KEY)
//                            .name("roles")
//                            .roles(roleList)
//                            .build();
//        rAdminRepository.save(rAdmin);
//        // A hash to store type
//        // A hash to store roles with url/methods
//
//        SystemConfig.ROLES = rAdminRepository.findOne(SystemConfig.R_ROLES_KEY);
//        if (SystemConfig.ROLES == null) {
//            throw new FactoryBeanNotInitializedException("Role list is null");
//        }

//        Read/Write from redis directly
//        PermissionInfo p = PermissionInfo.builder().permisionKey("key").permissionName("name").description("de").build();
//        Map<String, PermissionInfo> mp = new HashMap<String, PermissionInfo>();
//        Map<String, Map<String, PermissionInfo>> mp2 = new HashMap<String, Map<String, PermissionInfo>>();
//        mp.put("users_post", p);
//        mp2.put("admin", mp);
//        RoleDicts test = RoleDicts.builder().roles(mp2).build();

//        Gson gson = new GsonBuilder().create();
//        Gson gson = new Gson();

//        stringRedisTemplate.opsForValue().set(SystemConfig.ROLE_DICTS_KEY, gson.toJson(test));

        // Get permission enable switch
        String permissionsSwitch = stringRedisTemplate.opsForValue().get(SystemConfig.PERMISSION_SWITCH_KEY);
        SystemConfig.PERMISSION_SWITCH = Boolean.parseBoolean(permissionsSwitch);
        LOGGER.info("enable permission check..." + SystemConfig.PERMISSION_SWITCH.toString());

        // Get permission dicts
        String permissionsDict = stringRedisTemplate.opsForValue().get(SystemConfig.ROLE_DICTS_KEY);
        if (SystemConfig.PERMISSION_SWITCH && (permissionsDict == null || permissionsDict.isEmpty() ) ) {
            throw new ExceptionInInitializerError("Can not get permission data from redis");
        }

        SystemConfig.ROLE_DICTS = gson.fromJson(permissionsDict, RoleDicts.class);
        LOGGER.info("get permission dict from redis, is empty?..." + (permissionsDict == null || permissionsDict.isEmpty()));

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

        return () -> {};
    }

}
