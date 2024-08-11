package com.thomasvitale.multitenant.sample.config;

import java.io.IOException; 
import java.util.Optional;


import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse; 
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
  
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CorsFilter implements Filter {
    public final static String CONTENT_SECURITY_POLICY = Optional.ofNullable(System.getenv("CONTENT_SECURITY_POLICY"))
            .orElse("default-src 'self'; script-src 'self' https://trusted-cdn.com 'sha256-4IiDsMH+GkJlxivIDNfi6qk0O5HPtzyvNwVT3Wt8TIw='; style-src 'self' https://trusted-cdn.com; img-src 'self' https://trusted-cdn.com data:; connect-src 'self' https://api.trusted.com; font-src 'self' https://fonts.gstatic.com; media-src 'self'; manifest-src 'self'; worker-src 'self'; form-action 'self'; frame-src 'self'; frame-ancestors 'self'; object-src 'none';");
   
    public final static String ALLOWED_ALL = Optional.ofNullable(System.getenv("ALLOWED_ORIGINS"))
            .orElse(Optional.ofNullable(System.getProperty("web.cors.allowOrigins")).orElse("*"));
    
    public final static Set<String> ALLOWED_ORIGINS = Optional.ofNullable(System.getenv("ALLOWED_ORIGINS"))
            .map(origins -> Arrays.stream(origins.split(","))
                    .map(String::trim)
                    .filter(origin -> !origin.isEmpty())
                    .collect(Collectors.toSet()))
            .orElse(new HashSet<>());


    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");
        if (ALLOWED_ORIGINS.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "X-idp, x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN");
        response.addHeader("Access-Control-Expose-Headers", "xsrf-token, Content-Disposition");

        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Content-Security-Policy", CONTENT_SECURITY_POLICY);

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(req, resp);
        }
    }
}
