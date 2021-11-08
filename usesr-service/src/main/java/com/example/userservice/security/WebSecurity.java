package com.example.userservice.security;

import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    //bCryptPasswordEncoder와 userService가져오기
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    //생성된 JWT의 정보들을 가져오기위해 (application.yml 파일의 내용들을 가져오기위해) environment 추가
    private Environment env;


    @Autowired
    public WebSecurity(Environment env,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       UserService userService
                      ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.env = env;
    }

    //인증 메소드 재정의
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 사용하지않음
        http.csrf().disable();
        // uri가 /users/** 이면 인증을 모두 허용
//        http.authorizeRequests().antMatchers("/users/**").permitAll();
        
        //actuator에 대해서는 인증을 거치지 않고 모두 허용
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        // 인증이 되어진 상태에서만 그 이후의 기능들을 제공되게 할 것임.
        http.authorizeRequests().antMatchers("/**")
                .hasIpAddress("192.168.2.112")//ip를 제한적으로 받는다.
                .and()
                .addFilter(getAuthenticationFilter()); // 인증처리를 하기위한 filter


        //h2-console로 접속하게 될 시, 프레임으로 데이터가 나뉘어져 있기에 이를 무시할 수 있게끔 설정
        http.headers().frameOptions().disable();

    }

    //인증처리
    private AuthenticationFilter getAuthenticationFilter() throws Exception{

        AuthenticationFilter authenticationFilter
                = new AuthenticationFilter(authenticationManager(), userService, env); //인스턴스 생성
//        authenticationFilter.setAuthenticationManager(authenticationManager()); //필터에 작업할 수 있는 매니저를 넣기.

        return authenticationFilter;
    }



    //인증처리를 하기 위한 configuration 메소드, authentication manager가 가지고 잇는 userDetailService를 이용해서
    //db에 저장되어 있는 encrypted pw와 사용자가 입력하는 input_pw를 비교하려고한다. => input_pw를 encrypted로,
    // sql를 처리해주는 것인 userDetailService이고, 사용자를 검색해오는 비즈니스로직을 가진 것이 userService이다.
    // 위의 과정이 잘 끝나면 passwordEncoder()를 이용해 input_pw를 변환처리한다.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder); // email을 통해 사용자를 검색해옴

    }



}




