package com.studyolle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    //TODO 2021.01.10 9.회원가입 패스워드 인코딩
    //     스프링 시큐리티가 권장하는 passwordEncoder 를 사용하기 위한 빈 등록
    //     PasswordEncoderFactories.createDelegatingPasswordEncoder()
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
