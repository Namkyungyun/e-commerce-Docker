package com.example.userservice.service;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if(userEntity == null)
            throw new UsernameNotFoundException(username);

        return new User(userEntity.getEmail(),userEntity.getEncryptedPwd(), true, true, true, true, new ArrayList<>());

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

    @Override
    public UserDTO getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if(userEntity == null)
            throw new UsernameNotFoundException("User not fount");

        UserDTO userDTO = new ModelMapper().map(userEntity, UserDTO.class);
        //주문서비스 완성시키기
        List<ResponseOrder> orders = new ArrayList<>();
        userDTO.setOrders(orders);

        return userDTO;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();    //전체데이터가져오기

    }

}