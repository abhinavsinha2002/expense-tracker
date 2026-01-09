package com.abhinav.expense_tracker.security;
import java.io.IOException;

import org.springframework.beans.factory.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;
import com.abhinav.expense_tracker.service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired private JwtUtil jwtUtil;
    @Autowired private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)
    throws IOException, jakarta.servlet.ServletException{
        final String authHeader = req.getHeader("Authorization");
        String username = null;
        String token = null;
        if(authHeader!=null && authHeader.startsWith("Bearer ")){
            token=authHeader.substring(7);
            try{
                username = jwtUtil.extractUsername(token);
            }
            catch(Exception ex){

            }
        }
        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails ud=userDetailsService.loadUserByUsername(username);
            if(jwtUtil.validateToken(token)){
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(ud, null,ud.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(req, res);
    }

}
