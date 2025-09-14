package com.example.expensetracker.security;

import com.example.expensetracker.entity.User;
import com.example.expensetracker.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;


import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;
    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
      try {
          String header = request.getHeader("Authorization");
          String token = null;
          if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
              token = header.substring(7);
          }
          if (token != null && jwtUtil.validateToken(token)) {
              String username = jwtUtil.getUsernameFromToken(token);
              User user = userService.loadByUsername(username);
              if (user != null) {
                  UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                          username, null, Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
                  SecurityContextHolder.getContext().setAuthentication(auth);
              }
          }
          filterChain.doFilter(request, response);
      }catch (Exception ex){
          handlerExceptionResolver.resolveException(request,response,null,ex);

      }
    }
}
