package com.abhinav.expense_tracker.service;

import java.nio.file.attribute.UserPrincipal;
import java.util.Optional;

import javax.naming.AuthenticationException;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

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

        if("google".equals(registrationId)){
            email = (String)attributes.get("email");
            name = (String)attributes.get("name");
            providerId = (String)attributes.get("sub");
        }
        else if("github".equals(registrationId)){
            email = (String)attributes.get("email");
            name = (String)attributes.get("name");
            providerId = (String)attributes.get("id").toString();
        }

        User user = saveOrUpdateUser(email,name,providerId,registrationId);

        return UserPrincipal.create(user,attributes);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User){
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
            oAuth2UserRequest.getClientRegistration().getRegistrationId(),
            oAuth2User.getAttributes()
        );

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if(userOptional.isPresent()){
            user = userOptional.get();
            user = updateExistingUser(user,oAuth2UserInfo);
        }
        else{
            user = registerNewUser(oAuth2UserRequest,oAuth2UserInfo);
        }

        return UserPrincipal.create(user,oAuth2User.getAttributes());
    }
}
