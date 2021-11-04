package com.example.usesrservice.controller;

import com.example.usesrservice.dto.UserDTO;
import com.example.usesrservice.jpa.UserEntity;
import com.example.usesrservice.service.UserService;
import com.example.usesrservice.vo.Greeting;
import com.example.usesrservice.vo.RequestUser;
import com.example.usesrservice.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user-service")
public class UserController {

    private Environment env;
    private UserService userService;
    @Autowired
    public UserController(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    @Autowired
    private Greeting greeting;

    @GetMapping("/health_check")
    public String status(){

        return String.format("It's Working in User Service on PORT %s", env.getProperty("local.server.port"));
    }

    @GetMapping("/welcome")
    public String welcome(){
//        return env.getProperty("greeting.message");
        return greeting.getMessage();
    }
    @PostMapping("/users")
    public ResponseEntity createUser(@RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDTO userDTO = mapper.map(user, UserDTO.class);
        userService.createUser(userDTO);

        //유저에게 보이는 반환 body 내용을 같이 반환시키기 위해서 modelmapper로 userDto내용을 responseUser로 변환
        ResponseUser responseUser = mapper.map(userDTO, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }
    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers(){
        //1. UserEntity 가져오기
        Iterable<UserEntity> userList = userService.getUserByAll();
        //2. UserEntity -> REsponseUser로 변경하기 (반복문을 쓰기에 List로)
        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {
            result.add(new ModelMapper().map(v,ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId){
        //1. UserEntity 가져오기
        UserDTO userDTO = userService.getUserByUserId(userId);
        //2. UserEntity -> ResponseUser로 변경하기
        ResponseUser returnUser = new ModelMapper().map(userDTO, ResponseUser.class);


        return ResponseEntity.status(HttpStatus.OK).body(returnUser);
    }

}
