package com.abhinav.expense_tracker.service;
import java.util.Map;
import java.util.Optional;

import javax.naming.AuthenticationException;
import com.abhinav.expense_tracker.security.*;
import com.abhinav.expense_tracker.entity.AuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.abhinav.expense_tracker.entity.User;
import com.abhinav.expense_tracker.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    @Autowired 
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException{
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        Map<String,Object> attributes = oAuth2User.getAttributes();

        String email = "";
        String name = "";
        String providerId = "";
        String imageUrl = ""; 

        if("google".equals(registrationId)){
            email = (String)attributes.get("email");
            name = (String)attributes.get("name");
            providerId = (String)attributes.get("sub");
            imageUrl = (String)attributes.get("picture");
        }
        else if("github".equals(registrationId)){
            email = (String)attributes.get("email");
            name = (String)attributes.get("name");
            providerId = String.valueOf(attributes.get("id"));
            imageUrl = (String)attributes.get("avatar_url");
        }

        if(email == null && "github".equals(registrationId)) {
             throw new OAuth2AuthenticationException("Email not found from GitHub. Keep email public.");
        }

        User user = saveOrUpdateUser(email,name,providerId,registrationId,imageUrl);

        return UserPrincipal.create(user, attributes);
    }

    private User saveOrUpdateUser(String email,String name, String providerId,String provider,String imageUrl){
        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null){
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setProviderId(providerId);
            user.setAuthProvider(AuthProvider.valueOf(provider.toUpperCase()));
            user.setEnabled(true);
        }
        user.setUsername(name);
        user.setProfilePictureURL(imageUrl);
        return userRepository.save(user);
    }
}
