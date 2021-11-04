package com.example.usesrservice.service;

import com.example.usesrservice.dto.UserDTO;
import com.example.usesrservice.jpa.UserEntity;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);

    //유저아이디로 상세조회
    UserDTO getUserByUserId(String userId);
    //반복적인데이터
    Iterable<UserEntity> getUserByAll();
}
