package com.microservices.support.filters;

import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;

public class ResponseFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String url = ctx.getRequest().getRequestURL().toString();
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse res = ctx.getResponse();
        Collection<String> headerNames = res.getHeaderNames();
        for (String headerName : headerNames) {
            System.out.println(headerName + " " + res.getHeader(headerName));
        }
        log.info(String.format("responseFilter... %s request to %s", request.getMethod(), request.getRequestURL().toString()));
        log.info(String.format("remote addr... %s", request.getRemoteAddr()));

        // add cors header
        if (res.getHeader("Access-Control-Allow-Credentials") == null)
            ctx.addZuulResponseHeader("Access-Control-Allow-Credentials", "true");
        if (res.getHeader("Access-Control-Allow-Origin") == null) {
            String reqOriginHeader = ctx.getRequest().getHeader("Origin");
            ctx.addZuulResponseHeader("Access-Control-Allow-Origin", reqOriginHeader);
        }
//        if (res.getHeader("Access-Control-Allow-Origin") == null)
//            ctx.addZuulResponseHeader("Access-Control-Allow-Origin", "*");

//        if (res.getHeader("Access-Control-Allow-Methods") == null) {
//            String method = res.getHeader("Access-Control-Request-Method");
//            ctx.addZuulResponseHeader("Access-Control-Allow-Methods", method);
//        }

        //add headers to disable browser caceh
        if (res.getHeader("Cache-Control") == null)
            ctx.addZuulResponseHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        if (res.getHeader("Pragma") == null)
            ctx.addZuulResponseHeader("Pragma", "no-cache");
        if (res.getHeader("Expires") == null)
            ctx.addZuulResponseHeader("Expires", "0");

//        InputStream responseDataStream = ctx.getResponseDataStream();
//        final String responseData;
//        try {
//            responseData = CharStreams.toString(new InputStreamReader(responseDataStream));
//            ctx.setResponseBody("test");
//            responseDataStream.close();
//        } catch (IOException e) {
//            log.error(e.toString());
//        }

        return null;
    }

}
