package com.centralcfg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Created by yichen.wei on 6/25/17.
 */

@SpringBootApplication
//@EnableDiscoveryClient
@EnableAutoConfiguration
@EnableConfigServer
public class CentralConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(CentralConfigApplication.class, args);
    }
}
