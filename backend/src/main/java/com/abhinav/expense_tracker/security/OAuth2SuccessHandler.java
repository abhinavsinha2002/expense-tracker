package com.abhinav.expense_tracker.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
    @Autowired
    private JwtUtil jwtUtil;

    private static final String REDIRECT_URL = "http://localhost:4200/oauth2/redirect";
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
        HttpServletResponse response, Authentication authentication) throws IOException{
            String token = jwtUtil.generateToken(authentication.getName());
            String targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL)
                .queryParam("token", token)
                .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
}
