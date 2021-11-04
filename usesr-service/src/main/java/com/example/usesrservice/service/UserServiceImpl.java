package com.example.usesrservice.service;

import com.example.usesrservice.dto.UserDTO;
import com.example.usesrservice.jpa.UserEntity;
import com.example.usesrservice.jpa.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {


    UserRepository userRepository;  //저장하기 위해서(생성자를 사용해도 좋음)
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDTO createUser(UserDTO userDTO) {
        userDTO.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //DTO에서 Entity로 변환될 수 있도록 모델 맵퍼를 이용한다.
        UserEntity userEntity = mapper.map(userDTO, UserEntity.class); //userDTO를 UserEntity.class로 변환시키겠다.
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDTO.getPwd())); //비밀번호 암호화

        userRepository.save(userEntity);

        UserDTO returnUserDTO = mapper.map(userEntity, UserDTO.class);

        return returnUserDTO;
    }

}