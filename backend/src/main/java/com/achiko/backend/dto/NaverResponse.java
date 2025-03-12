package com.achiko.backend.dto;

import java.util.Map;

public class NaverResponse implements OAuth2Response{

    private final Map<String, Object> attribute;	// accessToken을 톹해 얻은 사용자 데이터는 attribute 형태로 들어옴

    public NaverResponse(Map<String, Object> attribute) {

        this.attribute = (Map<String, Object>) attribute.get("response");	// 네이버의 경우 attribute 안의 response안에 데이터가 있음
    }

    @Override
    public String getProvider() {

        return "naver";
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {

        return attribute.get("email").toString();
    }

    @Override
    public String getName() {

        return attribute.get("name").toString();
    }
}