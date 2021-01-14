package com.studyolle.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//TODO 스프링 시큐리티 설정

//TODO 2021.01.13 18.가입 확인 이메일 재전송
//     1. "/check-email" 은 인증된 유저만 접근이 가능함으로 스프링 시큐리티에서
//        인증이 필요하지 않은 접근에서 제외

@Configuration
//TODO 스프링 시큐리티 설정을 직접하겠다라는 애노테이션
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //TODO 아래 요청들은 GET, POST 요청에 대해서 인증 과정을 거치지 않고 페이지 요청 허용
                .mvcMatchers("/", "/login", "/sign-up",
                        "/check-email-token", "/email-login", "/check-email-login",
                        "/login-link").permitAll()
                //TODO 프로필 요청의 경우 GET 요청에서만 허용
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
                //TODO 이외 나머지 설정들은 로그인을 해야만 사용할 수 있다.
                .anyRequest().authenticated();

        //TODO 2021.01.14 로그인 / 로그아웃
        //     1. 스프링 시큐리티가 사용하는 로그인 폼을 개발자가 만든 로그인 폼으로
        //        사용하도록 설정
        http.formLogin()
                .loginPage("/login").permitAll();

        //TODO 2021.01.14 로그인 / 로그아웃
        //     1. 로그아웃 했을때 화면을 메인화면 ( templates/index.html ) 로 이동하도록 설정
        http.logout()
                .logoutSuccessUrl("/");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //TODO resources/static 에 있는 리소스들은 스프링 시큐리티를 적용하지 말라고 정의
        //     흔히 사용하는 staticResources 경로 .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        // ----------------------------------------------------------------------------------------------------------
        //     2021.01.12 14.프론트엔드 라이브러리 설정
        //     1. npm 으로 bootstrap 을 다운 받고 html 파일 수정
        //     2. 스프링 시큐리티에 추가적으로 /node_modules/** 를 추가하여 js, css 등의 파일은
        //        인증 요청하지 않게 수정 - .mvcMatchers("/node_modules/**")
        web.ignoring()
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
