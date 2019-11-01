package com.cngc.admin.security.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author maxD
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UrlTokenConverterFilter implements Filter {
    private final static String AUTH_HEADER_NAME = "Authorization";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String _token;
        if (StringUtils.isEmpty(httpServletRequest.getHeader(AUTH_HEADER_NAME)) &&
                !StringUtils.isEmpty(_token = httpServletRequest.getParameter("_token"))) {
            final String token = OAuth2AccessToken.BEARER_TYPE.toLowerCase() + " " + _token;
            chain.doFilter(new HttpServletRequestWrapper(httpServletRequest) {
                @Override
                public Enumeration<String> getHeaders(String name) {
                    if(AUTH_HEADER_NAME.equals(name)) {
                        List<String> headers = new ArrayList<>();
                        headers.add(token);
                        return Collections.enumeration(headers);
                    }
                    return super.getHeaders(name);
                }

                @Override
                public String getHeader(String name) {
                    if (AUTH_HEADER_NAME.equals(name)) {
                        return token;
                    }
                    return super.getHeader(name);
                }

                @Override
                public Enumeration getHeaderNames() {
                    List<String> names = Collections.list(super.getHeaderNames());
                    names.add(AUTH_HEADER_NAME);
                    return Collections.enumeration(names);
                }
            }, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
