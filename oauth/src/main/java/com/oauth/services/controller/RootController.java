package com.oauth.services.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Enumeration;

@Controller
@RequestMapping("/baseinfo")
public class RootController {


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String getRootInfo(HttpServletRequest request,
                              HttpServletResponse response, Model model) {
        Enumeration<String> headerNames = request.getHeaderNames();
        System.out.println("-------------");
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + " " + request.getHeader(headerName));
        }
        return "{\"info\": \"Auth server !\"}";
    }

}
