package com.studyolle.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
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

    //TODO 2021.01.17 31.ModelMapper 적용
    //     1. ModelMapper 추가 설정
    //      1). Destination 이름이 UnderScore 가 아니면 하나의 프로퍼티로 간주
    //       ex). studyCreateByEmail 같이 Camel Case 로 작성이 되어있으면 다른 프로퍼티를 찾지 않도록 설정
    //      2). Source 이름이 UnderScore 가 아니면 하나의 프로퍼티로 간주
    //       ex). studyCreateByEmail 같이 Camel Case 로 작성이 되어있으면 다른 프로퍼티를 찾지 않도록 설정
    //     2. 이와 같은 설정이 없으면 의도와 다른 결과를 도출
    //       ex). studyCreateByEmail 프로퍼티에 복사해야하는데 email 프로퍼티에 복사하려고 시도하는 경우
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
        return modelMapper;
    }
}
