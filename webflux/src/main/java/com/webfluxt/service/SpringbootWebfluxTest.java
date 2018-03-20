package com.webfluxt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.http.server.HttpServer;

import java.io.IOException;


@Configuration
@ComponentScan
@EnableWebFlux
//Did not work for yml https://stackoverflow.com/questions/21271468/spring-propertysource-using-yaml
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = false)
public class SpringbootWebfluxTest {

    @Value("${server.port:8089}")
    private int port = 8080;

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                SpringbootWebfluxTest.class)) {

            context.getBean(NettyContext.class).onClose().block();
        }
    }

//    @Profile("default")
    @Bean
    public NettyContext nettyContext(ApplicationContext context) {

        HttpHandler handler = WebHttpHandlerBuilder.applicationContext(context).build();
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(handler);
        System.out.println("read from config...");
        System.out.println(this.port);
        System.out.println(env.getProperty("server.port"));
        HttpServer httpServer = HttpServer.create("localhost", this.port);
        return httpServer.newHandler(adapter).block();
    }

}

