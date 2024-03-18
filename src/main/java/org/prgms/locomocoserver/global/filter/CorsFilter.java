package org.prgms.locomocoserver.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;

@Slf4j
@Component
public class CorsFilter implements Filter {

    private static HashSet<String> alloweOrigin = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        alloweOrigin.add("http://localhost:3000");
        alloweOrigin.add("https://locomoco.kro.kr");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        log.info("CorsFilter.doFilter START");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin") == null ? "https://locomoco.kro.kr" : request.getHeader("Origin");
        log.info("CorsFilter Origin : " + origin);
        if(alloweOrigin.contains(origin) == false) return;
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Authorization, provider");
        response.setHeader("Access-Control-Expose-Headers", "Authorization, provider");

        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.info("CorsFilter.doFilter OPTION called");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }
}
