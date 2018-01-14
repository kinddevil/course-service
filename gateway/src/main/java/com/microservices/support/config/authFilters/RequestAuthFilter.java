package com.microservices.support.config.authFilters;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.util.Enumeration;

//@Order(2)
public class RequestAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + " " + request.getHeader(headerName));
        }
        // Cors
        if (request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS)) {
            response.resetBuffer();
            HttpServletResponse hsr = (HttpServletResponse) response;
            // add cors header
            if (response.getHeader("Access-Control-Allow-Credentials") == null)
                hsr.setHeader("Access-Control-Allow-Credentials", "true");

            if (response.getHeader("Access-Control-Allow-Origin") == null)
                hsr.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));

            if (response.getHeader("access-control-request-headers") == null)
                hsr.setHeader("Access-Control-Allow-Headers", request.getHeader("access-control-request-headers"));

            if (response.getHeader("Access-Control-Request-Method") == null)
                hsr.setHeader("Access-Control-Request-Method", request.getHeader("Access-Control-Request-Method"));

            if (response.getHeader("Access-Control-Allow-Methods") == null) {
                String method = response.getHeader("Access-Control-Request-Method");
                hsr.setHeader("Access-Control-Allow-Methods", method);
            }

            hsr.setStatus(200);
            return;
        }

        // Auth
        // X-Real-Client-Ip: 8.8.8.8
        // X-Hulu-Request-Id: ...
//        throw new SecurityException("");
//        filterChain.doFilter(request, response);
//        new ResponseEntity<>(HttpStatus.BAD_REQUEST);

//        response.resetBuffer();
//        response.getOutputStream().write("{\"a\": 12}".getBytes());
//        HttpServletResponse hsr = (HttpServletResponse) response;
//        hsr.setStatus(401);
//        return;

        filterChain.doFilter(request, response);
    }
}
