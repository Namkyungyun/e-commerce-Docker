package com.example.usesrservice.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    //인증 메소드 재정의
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 사용하지않음
        http.csrf().disable();
        // uri가 /users/** 이면 인증을 모두 허용
        http.authorizeRequests().antMatchers("/users/**").permitAll();

        //h2-console로 접속하게 될 시, 프레임으로 데이터가 나뉘어져 있기에 이를 무시할 수 있게끔 설정
        http.headers().frameOptions().disable();

    }




}
