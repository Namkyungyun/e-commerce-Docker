package com.example.userservice.security;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Component
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService,
                                Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    //인증 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        try{
            //inputStream으로 읽는 이유는 전달시켜주고자하는 로그인의 값이 포스트형태로 전달되므로 Request파라미터로 받을 수 없기때문이다.
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(),RequestLogin.class);

            //인증정보를 만들기 위해서, 필터로 전달을 해야하고, 위의 값을 사용하기 위해서 Authentication 토큰으로 변경을 시켜야한다.
            //spring sercurity에서 값을 사용하기 위해서는 토큰으로의 변경이 이루어져야하고, 따라서 UsernamePassWordAuthenticationToken을 사용한다.
            return getAuthenticationManager().authenticate(
                    //어떠한 권한을 가질 것인지를 전달하는 파라미터를 담아두는 것.
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>())
            );

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    //인증 성공(로그인성공 시, 정확하게 어떠한 처리를 해줄 것인지 (토큰을 만든다거나, 토큰만료시간 등의 처리))
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult
                                            ) throws IOException, ServletException {
        String userName = ((User)authResult.getPrincipal()).getUsername(); //성공 후 이메일값
        UserDTO userDetails = userService.getUserDetailsByEmail(userName); //이메일값으로 전체dto데이터

        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())                                //userId로 토큰 만듦
                .setExpiration(new Date(System.currentTimeMillis() +                //토큰 유효기간(뒷단이 string이므로 숫자로 parse)
                        Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))//암호화: 알고리즘 넣기
                .compact();


        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getUserId());




    }
}
