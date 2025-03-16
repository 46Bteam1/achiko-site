
package com.achiko.backend.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2Response oAuth2Response;
    private final String role;

    public CustomOAuth2User(OAuth2Response oAuth2Response, String role) {

        this.oAuth2Response = oAuth2Response;
        this.role = role;
    }

    @Override
    public Map<String, Object> getAttributes() {
    	return Map.of(
                "provider", oAuth2Response.getProvider(),
                "providerId", oAuth2Response.getProviderId(),
                "email", oAuth2Response.getEmail(),
                "name", oAuth2Response.getName()
                );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return role;
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return oAuth2Response.getProvider()+"_"+oAuth2Response.getProviderId();
    }
    
    public String getRealname() {
    	
    	return oAuth2Response.getName();
    }

    public String getUsername() {

        return oAuth2Response.getProvider()+"_"+oAuth2Response.getProviderId();
    }
    
    public String getProvider() {
    	return oAuth2Response.getProvider();
    }
}