package com.studyolle.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//TODO 컨트롤러부터 아래 모든 클래스 테스트

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("회원 가입 화면 보이는지 테스트")
    void signUpFormTest() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));

//TODO    내 코드 - 테스트 실패 : content() 메서드는 html 본문을 가져오는 메서드임으로 view 의 이름이 "account/sign-up" 인지
//                검증하는데 부적학함 - view().name("account/sign-up") 으로 수정하여 테스트 완성
//                .andExpect(content().string("account/sign-up"));
    }

    @Test
    @DisplayName("회원 가입 화면에서 모델이 전달됬는지 테스트")
    void signUpFormV2Test() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }
}