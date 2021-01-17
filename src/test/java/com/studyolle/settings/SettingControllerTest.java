package com.studyolle.settings;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//TODO 2021.01.16 26.프로필 수정 테스트

@SpringBootTest
@AutoConfigureMockMvc
class SettingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void after() {
        accountRepository.deleteAll();
    }

    //TODO 2021.01.16 26.프로필 수정 테스트
    //     1. 인증된 사용자만 접근할 수 있기 때문에 WithAccount("youngbin") 이 없으면
    //        로그인 페이지로 전환하게 된다.
    @WithAccount("youngbin")
    @Test
    @DisplayName("프로필 수정 - 폼 보여주기")
    void 프로필_수정_폼_보여주기() throws Exception {
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(view().name("settings/profile"));
    }

    //TODO 2021.01.16 26.프로필 수정 테스트
    //     1. 인증된 사용자 정보를 사용하기 위해 스프링 시큐리티가 제공하는 @WithUserDetails 애노테이션 사용
    //      1). 사용 방법 : @WithUserDetails(value = "youngbin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    //      2). JUnit5 에서는 정상적으로 지원하지 않아 사용 불가능
    //      3). 이유 : @BeforeEach 이후에 실행되어야 정상적으로 사용 가능한데
    //                JUnit5 에서는 @WithUserDetails 애노테이션이 먼저 실행하여
    //                value 에 해당하는 유저정보를 가져오려다가 오류 발생
    //      4). 해결방법 : 스프링 시큐리티가 제공하는 모든것을 제어하는 마지막 기능을 활용
    //                    @WithSecurityContext 사용 - CustomAnnotation 을 작성
    //      5). studyolle.WithAccount, studyolle.WithAccountSecurityFactory 참고
    //@WithUserDetails(value = "youngbin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @WithAccount("youngbin")
    @Test
    @DisplayName("프로필 수정하기 - 한줄소개 입력값 정상")
    void 프로필_수정_한줄소개_입력값_정상() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post("/settings/profile")
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Account findAccount = accountRepository.findByNickname("youngbin");
        Assertions.assertThat(findAccount.getBio()).isEqualTo(bio);
    }

    @WithAccount("youngbin")
    @Test
    @DisplayName("프로필 수정하기 - 한줄소개 입력값 에러")
    void 프로필_수정_한줄소개_입력값_에러() throws Exception {
        String bio = "길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우.";
        mockMvc.perform(post("/settings/profile")
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account findAccount = accountRepository.findByNickname("youngbin");
        Assertions.assertThat(findAccount.getBio()).isNull();
    }

    @WithAccount("youngbin")
    @Test
    @DisplayName("프로필 수정하기 - 링크 입력값 정상")
    void 프로필_수정_링크_입력값_정상() throws Exception {
        String url = "http://youngbin.com";
        mockMvc.perform(post("/settings/profile")
                .param("url", url)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Account findAccount = accountRepository.findByNickname("youngbin");
        Assertions.assertThat(findAccount.getUrl()).isEqualTo(url);
    }

    @WithAccount("youngbin")
    @Test
    @DisplayName("프로필 수정하기 - 링크 입력값 에러")
    void 프로필_수정_링크_입력값_에러() throws Exception {
        String url = "http://dasdnqlkenqkwldnklvvnklaeqprqnchalrqlmcaaklasd.com";
        mockMvc.perform(post("/settings/profile")
                .param("url", url)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account findAccount = accountRepository.findByNickname("youngbin");
        Assertions.assertThat(findAccount.getUrl()).isNull();
    }

    @WithAccount("youngbin")
    @Test
    @DisplayName("프로필 수정하기 - 직업 입력값 정상")
    void 프로필_수정_직업_입력값_정상() throws Exception {
        String occupation = "개발자";
        mockMvc.perform(post("/settings/profile")
                .param("occupation", occupation)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Account findAccount = accountRepository.findByNickname("youngbin");
        Assertions.assertThat(findAccount.getOccupation()).isEqualTo(occupation);
    }

    @WithAccount("youngbin")
    @Test
    @DisplayName("프로필 수정하기 - 직업 입력값 에러")
    void 프로필_수정_직업_입력값_에러() throws Exception {
        String occupation = "개발자개발자개발자개발자개발자개발자개발자개발자개발자개발자개발자개발자" +
                "개발자개발자개발자개발자개발자개발자개발자개발자개발자개발자개발자개발자개발자";
        mockMvc.perform(post("/settings/profile")
                .param("occupation", occupation)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account findAccount = accountRepository.findByNickname("youngbin");
        Assertions.assertThat(findAccount.getOccupation()).isNull();
    }

    @WithAccount("youngbin")
    @Test
    @DisplayName("프로필 수정하기 - 활동지역 입력값 정상")
    void 프로필_수정_활동지역_입력값_정상() throws Exception {
        String location = "인천";
        mockMvc.perform(post("/settings/profile")
                .param("location", location)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Account findAccount = accountRepository.findByNickname("youngbin");
        Assertions.assertThat(findAccount.getLocation()).isEqualTo(location);
    }

    @WithAccount("youngbin")
    @Test
    @DisplayName("프로필 수정하기 - 활동지역 입력값 에러")
    void 프로필_수정_활동지역_입력값_에러() throws Exception {
        String location = "인천인천인천인천인천인천인천인천인천인천인천인천인천인천인천인천인천인천인천인천" +
                "인천인천인천인천인천인천인천인천인천인천인천인천인천인천" +
                "인천인천인천인천인천인천인천인천인천인천인천인천인천인천";
        mockMvc.perform(post("/settings/profile")
                .param("location", location)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account findAccount = accountRepository.findByNickname("youngbin");
        Assertions.assertThat(findAccount.getLocation()).isNull();
    }

    //TODO 2021.01.17 29. 패스워드 수정 테스트
    //     1. 패스워드 변경 - 입력값 정상 테스트
    @WithAccount("youngbin")
    @Test
    @DisplayName("패스워드 변경 - 입력값 정상")
    void 패스워드_변경_입력값_정상() throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("newPassword", "789456123")
                .param("newPasswordConfirm", "789456123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("message"));

        Account findAccount = accountRepository.findByNickname("youngbin");
        boolean passwordMatches = passwordEncoder.matches("789456123", findAccount.getPassword());

        Assertions.assertThat(passwordMatches).isTrue();
    }

    //TODO 2021.01.17 29. 패스워드 수정 테스트
    //     1. 패스워드 변경 - 입력값 에러 테스트
    @WithAccount("youngbin")
    @Test
    @DisplayName("패스워드 변경 - 입력값 에러")
    void 패스워드_변경_입력값_에러() throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("newPassword", "789456123")
                .param("newPasswordConfirm", "111111111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    //TODO 2021.01.17 29. 패스워드 수정 테스트
    //     1. 패스워드 변경 - 입력값 글자수 이상 테스트
    @WithAccount("youngbin")
    @Test
    @DisplayName("패스워드 변경 - 입력값 글자수 이상")
    void 패스워드_변경_입력값_글자수_이상() throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("newPassword", "789456123")
                .param("newPasswordConfirm", "789456")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    //TODO 2021.01.17 29. 패스워드 수정 테스트
    //     1. 패스워드 변경 - 폼 보여주기 테스트
    @WithAccount("youngbin")
    @Test
    @DisplayName("패스워드 변경 - 폼 보여주기")
    void 패스워드_변경_폼_보여주기() throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(view().name("settings/password"));
    }

    //TODO 2021.01.17 30.알림 설정
    //     1. 알림 설정 변경 테스트
    @WithAccount("youngbin")
    @Test
    @DisplayName("알림 설정 변경")
    void 알림_설정_변경() throws Exception {
        mockMvc.perform(post("/settings/notifications")
                    .param("studyCreatedByEmail", "true")
                    .param("studyCreatedByWeb", "true")
                    .param("studyEnrollmentResultByEmail", "true")
                    .param("studyEnrollmentResultByWeb", "true")
                    .param("studyUpdatedByEmail", "true")
                    .param("studyUpdatedByWeb", "true")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/notifications"))
                .andExpect(flash().attributeExists("message"));

        Account findAccount = accountRepository.findByNickname("youngbin");

        Assertions.assertThat(findAccount.isStudyCreatedByEmail()).isTrue();
        Assertions.assertThat(findAccount.isStudyCreatedByWeb()).isTrue();
        Assertions.assertThat(findAccount.isStudyEnrollmentResultByEmail()).isTrue();
        Assertions.assertThat(findAccount.isStudyEnrollmentResultByWeb()).isTrue();
        Assertions.assertThat(findAccount.isStudyUpdatedByEmail()).isTrue();
        Assertions.assertThat(findAccount.isStudyUpdatedByWeb()).isTrue();
    }

    //TODO 2021.01.17 30.알림 설정
    //     1. 알림 설정 폼 보여주기 테스트
    @WithAccount("youngbin")
    @Test
    @DisplayName("알림 설정 폼 보여주기")
    void 알림_설정_폼_보여주기() throws Exception {
        mockMvc.perform(get("/settings/notifications"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/notifications"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notifications"));
    }

    //TODO 2021.01.17 32.닉네임 수정
    @WithAccount("youngbin")
    @Test
    @DisplayName("닉네임 수정 - 입력값 정상")
    void 닉네임_수정_입력값_정상() throws Exception {
        mockMvc.perform(post("/settings/account")
                    .param("nickname", "binybiny")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/account"))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("message"));

        Account findAccount = accountRepository.findByNickname("binybiny");
        Assertions.assertThat(findAccount).isNotNull();
    }

    //TODO 2021.01.17 32.닉네임 수정
    @WithAccount("youngbin")
    @Test
    @DisplayName("닉네임 수정 - 입력값 에러")
    void 닉네임_수정_입력값_에러() throws Exception {
        mockMvc.perform(post("/settings/account")
                .param("nickname", "1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"))
                .andExpect(view().name("settings/account"));
    }

    //TODO 2021.01.17 32.닉네임 수정
    @WithAccount("youngbin")
    @Test
    @DisplayName("닉네임 수정 - 닉네임 중복")
    void 닉네임_수정_닉네임_중복() throws Exception {
        mockMvc.perform(post("/settings/account")
                .param("nickname", "youngbin")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"))
                .andExpect(view().name("settings/account"));
    }

    //TODO 2021.01.17 32.닉네임 수정
    @WithAccount("youngbin")
    @Test
    @DisplayName("닉네임 수정 - 폼 보여주기")
    void 닉네임_수정_폼_보여주기() throws Exception {
        mockMvc.perform(get("/settings/account"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"))
                .andExpect(view().name("settings/account"));
    }
}