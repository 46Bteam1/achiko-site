package com.achiko.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.achiko.backend.entity.UsersEntity;

public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
    @Query("SELECT u.bio FROM UserEntity u")
    List<String> findAllBios();
    @Query("SELECT u.bio FROM UsersEntity u WHERE u.isHost = :isHost")
    List<String> findAllBiosByUserType(@Param("isHost") int isHost);
    @Query("SELECT u.nickname, u.bio FROM UsersEntity u WHERE u.isHost = :isHost")
    List<Object[]> findUsersWithBiosByUserType(@Param("isHost") int isHost);
    @Query("SELECT u.bio FROM UsersEntity u WHERE u.loginId = :loginId")
    String findBioByLoginId(@Param("loginId") String loginId);
    @Query("SELECT u.nickname FROM UsersEntity u WHERE u.loginId = :loginId")
    String findNicknameByLoginId(@Param("loginId") String loginId);
    @Query("SELECT u.nickname, u.languages, u.age, u.nationality, u.religion, u.gender, u.profileImage FROM UsersEntity u WHERE u.nickname IN :nicknames")
    List<Object[]> findUserDetailsByNicknames(@Param("nicknames") List<String> nicknames);
    @Query("SELECT u.isHost FROM UsersEntity u WHERE u.loginId = :loginId")
    Integer findIsHostByLoginId(@Param("loginId") String loginId);
}