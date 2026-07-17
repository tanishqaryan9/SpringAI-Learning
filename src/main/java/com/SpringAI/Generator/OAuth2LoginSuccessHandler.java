package com.SpringAI.Generator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;

    @Value("${spring.app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            String login = oAuth2User.getAttribute("login");
            String username = (login != null) ? login : oAuth2User.getName();

            if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
                String provider = oauthToken.getAuthorizedClientRegistrationId();
                email = username + "@" + provider + ".oauth";
            } else {
                email = username + "@unknown.oauth";
            }
        }

        String token = email != null ? jwtUtils.generateTokenFromUsername(email) : null;

        if (token != null) {
            response.sendRedirect(frontendUrl + "/?token=" + token + "&email=" + email);
        } else {
            response.sendRedirect(frontendUrl + "/?error=oauth_failed");
        }
    }
}
