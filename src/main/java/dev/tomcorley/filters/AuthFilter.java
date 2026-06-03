package dev.tomcorley.filters;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        // Code that runs on the way in to the server
        System.out.println("Coming in ...");
        
        // Passes down to next filter in the chain
        // chain.doFilter(request, response);

        // Code that runs on the way out of the server
        // After controller has returned
        System.out.println("Sending out");
    }
}
