package com.achiko.backend.service;


import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.achiko.backend.dto.CustomOAuth2User;
import com.achiko.backend.dto.GoogleResponse;
import com.achiko.backend.dto.KakaoResponse;
import com.achiko.backend.dto.NaverResponse;
import com.achiko.backend.dto.OAuth2Response;
import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    //DefaultOAuth2UserService OAuth2UserService의 구현체
	
	private final UserRepository userRepository;
	
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();	// 네이버인지 구글인지 변수 받아옴
        OAuth2Response oAuth2Response = null;	// 네이버와 구글의 규격이 다르기 때문에 그걸 받을 수 있는 인터페이스 OAuth2Response
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("kakao")) {

            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }
        String username = oAuth2Response.getProvider()+"_"+oAuth2Response.getProviderId();
        String userNickname = oAuth2Response.getProvider()+"_"+oAuth2Response.getEmail().split("@")[0];	// provider + 이메일 주소의 @앞부분을 닉네임으로 설정
        UserEntity userEntity = userRepository.findByLoginId(username);

        String role = "user";
        boolean needsAdditionalInfo = false; // 추가 정보 입력 여부
        if (userEntity == null) {

            userEntity = new UserEntity();
            userEntity.setLoginId(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setRole(role);
            userEntity.setNickname(userNickname);
            userEntity.setRealName(oAuth2Response.getName());
            userEntity.setProvider(oAuth2Response.getProvider());
            
            // ✅ `age`, `nationality`, `religion`, `gender`, `bio` 값이 없으므로 추가 정보 입력 필요
            needsAdditionalInfo = true;
            userRepository.save(userEntity);
        }
        else {

        	userEntity.setLoginId(username);
        	userEntity.setEmail(oAuth2Response.getEmail());

            role = userEntity.getRole();
            // ✅ 추가 정보 입력이 필요한지 확인
            if (userEntity.getAge() == null || userEntity.getNationality() == null || 
                userEntity.getReligion() == null || userEntity.getGender() == null || 
                userEntity.getBio() == null) {
                needsAdditionalInfo = true;
            }
            userRepository.save(userEntity);
        }
        
        // ✅ `CustomOAuth2User` 생성 후 PrincipalDetails로 반환
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2Response, userEntity.getRole(), userEntity);

        // ✅ `PrincipalDetails`에 추가 정보 입력 필요 여부를 담아 반환
        PrincipalDetails principalDetails = new PrincipalDetails(customOAuth2User);

        // ✅ PrincipalDetails에 직접 needsAdditionalInfo 추가
        principalDetails.setNeedsAdditionalInfo(needsAdditionalInfo);
        return principalDetails;
    }
}