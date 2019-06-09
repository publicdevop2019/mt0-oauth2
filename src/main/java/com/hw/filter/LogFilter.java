package com.hw.filter;

import org.springframework.context.annotation.Description;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@Description("log incoming requests")
public class LogFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        System.out.print("URI:" + req.getRequestURI() + " Method:" + req.getMethod() + " headers::");
        Enumeration<String> headers = req.getHeaderNames();
        while (headers.hasMoreElements()) {
            String nextHeader = headers.nextElement();
            System.out.print("Header: " + nextHeader + " Value:" + req.getHeader(nextHeader) + " ");
        }
        System.out.print("\n");
        chain.doFilter(request, response);
    }
}
