package com.microservices.support;

import com.microservices.support.filters.ErrorFilter;
import com.microservices.support.filters.RequestFilter;
import com.microservices.support.filters.ResponseFilter;
import com.microservices.support.filters.RouteFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
@Controller
@EnableZuulProxy
@RestController
@EnableDiscoveryClient
@EnableFeignClients
//@EnableOAuth2Sso
public class ZuulApplication extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer, ErrorController {
	
    public static void main(String[] args) {
        new SpringApplicationBuilder(ZuulApplication.class).web(true).run(args);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {

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

    @RequestMapping(value = "/health_check")
    public String healthCheck() {
        return "ok";
    }

    @Override
    public String getErrorPath() {
        return "/health_check";
    }
}
