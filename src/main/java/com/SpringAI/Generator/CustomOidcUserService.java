package com.SpringAI.Generator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final ApplicationRepository repository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = oidcUser.getAttribute("email");

        if (email == null) {
            email = oidcUser.getName() + "@" + provider + ".oauth";
        }

        UserProfile user = repository.findByEmail(email);
        if (user == null) {
            user = new UserProfile();
            user.setProvider(provider);
            user.setEmail(email);
            user.setPassword(null);
            user.setRoles(List.of("ROLE_USER"));
            repository.save(user);
        } else if (user.getProvider() == null) {
            user.setProvider(provider);
            repository.save(user);
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}