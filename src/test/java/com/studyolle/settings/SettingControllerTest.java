package com.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.repository.TagRepository;
import com.studyolle.settings.form.TagForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;
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

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccountService accountService;

    @PersistenceContext
    EntityManager em;

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
        assertThat(findAccount.getBio()).isEqualTo(bio);
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
        assertThat(findAccount.getBio()).isNull();
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
        assertThat(findAccount.getUrl()).isEqualTo(url);
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
        assertThat(findAccount.getUrl()).isNull();
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
        assertThat(findAccount.getOccupation()).isEqualTo(occupation);
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
        assertThat(findAccount.getOccupation()).isNull();
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
        assertThat(findAccount.getLocation()).isEqualTo(location);
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
        assertThat(findAccount.getLocation()).isNull();
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

        assertThat(passwordMatches).isTrue();
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

        assertThat(findAccount.isStudyCreatedByEmail()).isTrue();
        assertThat(findAccount.isStudyCreatedByWeb()).isTrue();
        assertThat(findAccount.isStudyEnrollmentResultByEmail()).isTrue();
        assertThat(findAccount.isStudyEnrollmentResultByWeb()).isTrue();
        assertThat(findAccount.isStudyUpdatedByEmail()).isTrue();
        assertThat(findAccount.isStudyUpdatedByWeb()).isTrue();
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
        assertThat(findAccount).isNotNull();
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

    //TODO 2021.01.22 41.관심 주제 테스트
    //     1. 계정에
    @WithAccount("youngbin")
    @Test
    @DisplayName("계정의 태그 조회 폼 보여주기")
    void 태그_조회_폼_요청() throws Exception {
        mockMvc.perform(get("/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whitelist"));
    }

    //TODO 2021.01.22 41.관심 주제 테스트
    //     1. 테스트 주제
    //      1). 계정에 태그 추가 - 성공 테스트
    //     2. 테스트 목록
    //      1). 추가한 태그가 NotNull 인지
    //      2). 추가된 태그가 입력한 태그와 동일한지 (title)
    //      3). 계정 (Account) 가 가지고있는 AccountTag (중간 엔티티) 의 크기가 1 인지
    //     3. mockMvc.perform POST 요청으로 요청 본문에 json 담아 보내는 방법
    //        .contentType(MediaType.APPLICATION_JSON)
    //        .content(objectMapper.writeValueAsString(object)) 사용
    @WithAccount("youngbin")
    @Test
    @DisplayName("계정에 태그 추가 - 성공")
    @Transactional
    void 계정에_태그_추가_성공() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post("/settings/tags/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tagForm))
                    .with(csrf()))
                .andExpect(status().isOk());

        Tag findTag = tagRepository.findByTitle("newTag");
        Account findAccount = accountRepository.findByNickname("youngbin");

        assertThat(findTag).isNotNull();
        assertThat(findTag.getTitle()).isEqualTo("newTag");
        assertThat(findAccount.getAccountTags().size()).isEqualTo(1);
    }

    //TODO 2021.01.22 41.관심 주제 테스트
    //     1. 테스트 주제
    //      1). 계정에 태그 추가 - 중복 등록 실패 테스트
    //     2. 테스트 목록
    //      1). 중복등록된 경우 badRequest 를 반환 하도록 작성하여 이를 검증
    //       -. .andExpect(status().isBadRequest());
    //      2). 추가한 태그가 NotNull 인지
    //      3). 추가된 태그가 입력한 태그와 동일한지 (title)
    //      4). 계정 (Account) 가 가지고있는 AccountTag (중간 엔티티) 의 크기가 1 인지
    @WithAccount("youngbin")
    @Test @DisplayName("계정에 태그 추가 - 중복 등록 실패")
    @Transactional
    void 계정에_태그_추가_중복_등록_실패() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        Account account = accountRepository.findByNickname("youngbin");

        em.clear();
        em.flush();

        Tag tag = tagRepository.save(Tag.createTag(tagForm.getTagTitle()));

        accountService.addTag(account, tag);

        mockMvc.perform(post("/settings/tags/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        Tag findTag = tagRepository.findByTitle("newTag");
        Account findAccount = accountRepository.findByNickname("youngbin");

        assertThat(findTag).isNotNull();
        assertThat(findAccount.getAccountTags().size()).isEqualTo(1);
    }

    //TODO 2021.01.22 41.관심 주제 테스트
    //     1. 테스트 주제
    //      1). 계정에 태그 제거 테스트
    //     2. 테스트 목록
    //      1). 태그 제거 후 해당 이름의 태그가 데이터베이스에도 제거 됬는지 (AccountTag 로 확인)
    //      2). 태그 엔티티는 그대로 존재하는지
    //      3). 계정 (Account) 가 가지고있는 AccountTag (중간 엔티티) 의 크기가 0 인지
    @WithAccount("youngbin")
    @Test @DisplayName("계정에 태그 제거")
    @Transactional
    void 계정에_태그_제거() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        Account account = accountRepository.findByNickname("youngbin");

        Tag tag = tagRepository.save(Tag.createTag(tagForm.getTagTitle()));

        accountService.addTag(account, tag);

        mockMvc.perform(post("/settings/tags/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag findTag = tagRepository.findByTitle("newTag");
        Account findAccount = accountRepository.findByNickname("youngbin");

        assertThat(findTag).isNotNull();
        assertThat(findTag.getTitle()).isEqualTo("newTag");
        assertThat(findAccount.getAccountTags().size()).isEqualTo(0);
    }
}