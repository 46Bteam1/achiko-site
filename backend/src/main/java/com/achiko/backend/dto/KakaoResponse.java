package com.achiko.backend.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response{

    private final Map<String, Object> attribute;	// accessToken을 톹해 얻은 사용자 데이터는 attribute 형태로 들어옴
    private final Map<String, Object> kakaoAccountAttribute;
    private final Map<String, Object> profileAttribute;

    public KakaoResponse(Map<String, Object> attribute) {

        this.attribute = attribute;
        this.kakaoAccountAttribute = (Map<String, Object>) attribute.get("kakao_account");	// 카카오의 경우 attribute 안의 kakao_account안에 데이터가 있음
        this.profileAttribute = (Map<String, Object>) kakaoAccountAttribute.get("profile");	// kakao_account 안의 profile에 있는 데이터
    }

    @Override
    public String getProvider() {

        return "kakao";
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {

        return kakaoAccountAttribute.get("email").toString();
    }

    @Override
    public String getName() {

        return profileAttribute.get("nickname").toString();
    }
}