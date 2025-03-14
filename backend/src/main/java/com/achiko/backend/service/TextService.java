package com.achiko.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.achiko.backend.repository.UserReviewRepository;
import com.achiko.backend.repository.UsersRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TextService {
    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private UserReviewRepository userReviewRepository;

    public List<String> getAllUserBios(int isHost) {
        return userRepository.findAllBiosByUserType(isHost);
    }
    
    public Map<String, List<String>> getAllUserReputations(int isHost) {
        List<Object[]> userReputations = userReviewRepository.findAllUserReputationsByUserType(isHost);
        Map<String, List<String>> userReputationMap = new HashMap<>();

        for (Object[] row : userReputations) {
            String userNickname = (String) row[0];
            String comment = (String) row[1];

            userReputationMap.computeIfAbsent(userNickname, k -> new ArrayList<>()).add(comment);
        }

        List<Object[]> usersWithBios = userRepository.findUsersWithBiosByUserType(isHost);
        for (Object[] row : usersWithBios) {
            String userNickname = (String) row[0];
            String bio = (String) row[1];

            userReputationMap.putIfAbsent(userNickname, Collections.singletonList(bio));
        }

        return userReputationMap;
    }

    public Map<String, Map<String, Object>> getAllUserDetails(List<String> nicknames) {
        List<Object[]> userDetails = userRepository.findUserDetailsByNicknames(nicknames);
        Map<String, Map<String, Object>> userInfoMap = new HashMap<>();

        for (Object[] row : userDetails) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("languages", row[1]);
            userInfo.put("age", row[2]);
            userInfo.put("nationality", row[3]);
            userInfo.put("religion", row[4]);
            userInfo.put("gender", row[5]);
            userInfo.put("profileImage", row[6] != null ? row[6].toString() : "/default-profile.png");

            userInfoMap.put((String) row[0], userInfo); // Nickname as key
        }
        return userInfoMap;
    }


}