package com.studyolle.config;

import com.studyolle.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

//TODO 스프링 시큐리티 설정

//TODO 2021.01.13 18.가입 확인 이메일 재전송
//     1. "/check-email" 은 인증된 유저만 접근이 가능함으로 스프링 시큐리티에서
//        인증이 필요하지 않은 접근에서 제외

@Configuration
//TODO 스프링 시큐리티 설정을 직접하겠다라는 애노테이션
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //TODO 아래 요청들은 GET, POST 요청에 대해서 인증 과정을 거치지 않고 페이지 요청 허용
                .mvcMatchers("/", "/login", "/sign-up",
                        "/check-email-token", "/email-login", "/check-email-login",
                        "/login-link", "/login-by-email", "/logged-in-by-email", "my-html").permitAll()
                //TODO 프로필 요청의 경우 GET 요청에서만 허용
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
                //TODO 이외 나머지 설정들은 로그인을 해야만 사용할 수 있다.
                .anyRequest().authenticated();

        //TODO 2021.01.14 19.로그인 / 로그아웃
        //     1. 스프링 시큐리티가 사용하는 로그인 폼을 개발자가 만든 로그인 폼으로 사용하도록 설정
        http.formLogin()
                .loginPage("/login").permitAll();

        //TODO 2021.01.14 19.로그인 / 로그아웃
        //     1. 로그아웃 했을때 화면을 메인화면 ( templates/index.html ) 로 이동하도록 설정
        http.logout()
                .logoutSuccessUrl("/");

        //TODO 2021.01.15 21.로그인 기억하기
        //     1. 스프링 시큐리티 설정 : 해싱 기반 설정
        //     2. 해싱 쿠키 사용
        //      1). 해커가 쿠키를 탈취하여 로그인한 뒤 비밀번호를 변경하면 더 이상 희생자는 해당 계정을 사용하지 못함
        //      2). 안전하지 않은 방식
        //http.rememberMe()
        //        .key("asdkqnkrlq");

        //TODO 2021.01.15 21.로그인 기억하기
        //     1. 스프링 시큐리티 설정 : 보다 안전한 영속화 기반 설정
        //     2. 쿠키 발급 시 username, token(랜덤), series(랜덤, 고정) 의 정보를 이용
        //      1). 데이터베이스에 저장된 토큰과 인증 요청이 발생한 세션의 쿠키를 비교
        //      2). 인증을 할때마다 새로운 쿠키를 발급 ( username, series 고정, token 갱신 )
        //      3). 해커가 쿠키를 탈취하여 인증요청 - 로그인 - token 변경
        //      4). 희생자가 쿠키를 이용하여 인증요청 - 희생자가 가진 토큰과 데이터베이스의 토큰의 불일치 발생
        //      5). 이런 상황이 발생하면 이 쿠키는 탈취당한것이라 판단하고 데이터베이스의 토큰 삭제
        //       -. 해커와 희생자 모두 로그인 폼을 이용해서 로그인을 하도록 처리
        //     3. 조금더 개신된 방식
        http.rememberMe()
                .userDetailsService(accountService)
                .tokenRepository(tokenRepository());
    }

    //TODO 2021.01.15 21.로그인 기억하기
    //     1. 데이터베이스에 username, token, series 값을 저장하기위한 Repository 설정
    //      1). JdbcTokenRepositoryImpl 을 이용
    //      2). PersistentTokenRepository - 인터페이스
    //      3). JdbcTokenRepositoryImpl - 구현체
    //      4). DataSource 를 사용하기 위해 datasource Dependency Injection
    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
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
