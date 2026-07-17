package com.SpringAI.Generator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService{

    private final ApplicationRepository repository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException
    {
        OAuth2User oauth2User=super.loadUser(userRequest);

        String provider=userRequest.getClientRegistration().getRegistrationId();

        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        String email=oauth2User.getAttribute("email");

        if(email==null)
        {
            String login=oauth2User.getAttribute("login");
            email= ((login!=null)?login: oauth2User.getName())+"@"+provider+".oauth";
        }
        UserProfile user=repository.findByEmail(email);
        //If user is new then create user
        if(user==null)
        {
            user=new UserProfile();
            user.setProvider(provider);
            user.setEmail(email);
            user.setPassword(null);
            user.setRoles(List.of("ROLE_USER"));
            repository.save(user);
        }
        //If user is logged in with password but does not have provider, then set provider
        //now user can log in with both password and oauth2
        else if(user.getProvider()==null)
        {
            user.setProvider(provider);
            repository.save(user);
        }
        //Now user is created or saved in database but success handling is done by OAuth2LoginSuccessHandler
        //It will generate the token and lets it in the system, so spring calls it only once automatically
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        attributes.put("email", email);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new DefaultOAuth2User(authorities, attributes, nameAttributeKey);
    }

}
