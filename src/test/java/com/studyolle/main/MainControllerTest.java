package com.studyolle.main;

import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.dto.SignUpForm;
import com.studyolle.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    //TODO 2021.01.14 20.로그인 / 로그아웃 테스트
    //     1. 각 테스트가 실행하기전에 JUnit 프레임워크가 실행하는 BeforeEach 애노테이션을 사용해
    //        테스트의 중복코드를 제거
    @BeforeEach
    void setup() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("youngbin");
        signUpForm.setPassword("12345678");
        signUpForm.setEmail("yb@email.com");
        Account account = accountService.processNewAccount(signUpForm);
    }

    //TODO 2021.01.14 20.로그인 / 로그아웃 테스트
    //     1. BeforeEach 를 실행한 뒤 데이터가 그대로 남아있기 때문에
    //        데이터베이스를 비워주는 역할
    @AfterEach
    void after() {
        accountRepository.deleteAll();
    }

    //TODO 2021.01.14 20.로그인 / 로그아웃 테스트
    //     1. 테스트를 위한 param
    //      1). username : yb@email.com - 회원 가입할 때 사용한 이메일
    //      2). password : 12345678 - 회원 가입할 떄 사용한 비밀번호
    //      3). 폼 서브밋 요청하는것 임으로 with(csrf()) param 추가
    //     2. 테스트 항목
    //      1). 이메일로 로그인했을대 리다이렉션이 일어나는지
    //      2). 리다이렉트 Url 이 "/" 인지
    //      3). 로그인 후 해당 유저의 인증정보 중 이름이 로그인한 유저의 이름과 같은지
    @Test
    @DisplayName("로그인 (이메일) - 성공")
    void 로그인_이메일_성공() throws Exception {
        mockMvc.perform(post("/login")
                    .param("username", "yb@email.com")
                    .param("password", "12345678")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("youngbin"));
    }

    //TODO 2021.01.14 20.로그인 / 로그아웃 테스트
    //     1. 테스트를 위한 param
    //      1). username : youngbin - 회원 가입할 때 사용한 닉네임
    //      2). password : 12345678 - 회원 가입할 떄 사용한 비밀번호
    //      3). 폼 서브밋 요청하는것 임으로 with(csrf()) param 추가
    //     2. 테스트 항목
    //      1). 이메일로 로그인했을대 리다이렉션이 일어나는지
    //      2). 리다이렉트 Url 이 "/" 인지
    //      3). 로그인 후 해당 유저의 인증정보 중 이름이 로그인한 유저의 이름과 같은지
    @Test
    @DisplayName("로그인 (닉네임) - 성공")
    void 로그인_닉네임_성공() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "youngbin")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("youngbin"));
    }

    //TODO 2021.01.14 20.로그인 / 로그아웃 테스트
    //     1. 테스트를 위한 param
    //      1). username : ddd@email.com - 임의의 이메일
    //      2). password : 12345678 - 회원 가입할 떄 사용한 비밀번호
    //      3). 폼 서브밋 요청하는것 임으로 with(csrf()) param 추가
    //     2. 테스트 항목
    //      1). 이메일로 로그인했을대 리다이렉션이 일어나는지
    //      2). 리다이렉트 Url 이 "/login?error" 인지
    //      3). 인증정보가 unauthenticated() 인지
    @Test
    @DisplayName("로그인 - 실패")
    void 로그인_실패() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "ddd@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    //TODO 2021.01.14 20.로그인 / 로그아웃 테스트
    //     1. 테스트를 위한 param
    //      1). 폼 서브밋 요청하는것 임으로 with(csrf()) param 추가
    //     2. 테스트 항목
    //      1). 로그아웃 했을때 리다이렉션이 일어나는지
    //      2). 리다이렉트 Url 이 "/" 인지
    //      3). 인증정보가 unauthenticated() 인지
    @Test
    @DisplayName("로그아웃")
    void 로그아웃() throws Exception {
        mockMvc.perform(post("/logout")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }
}