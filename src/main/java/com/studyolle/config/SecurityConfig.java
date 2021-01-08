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

@Configuration
//TODO 스프링 시큐리티 설정을 직접하겠다라는 애노테이션
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //TODO 아래 요청들은 GET, POST 요청에 대해서 인증 과정을 거치지 않고 페이지 요청 허용
                .mvcMatchers("/", "/login", "/sign-up", "/check-email", "/check-email-token",
                        "/email-login", "/check-email-login", "/login-link").permitAll()
                //TODO 프로필 요청의 경우 GET 요청에서만 허용
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
                //TODO 이외 나머지 설정들은 로그인을 해야만 사용할 수 있다.
                .anyRequest().authenticated();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //TODO resources/static 에 있는 리소스들은 스프링 시큐리티를 적용하지 말라고 정의
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
