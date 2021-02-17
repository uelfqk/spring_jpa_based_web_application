package com.studyolle.study;

import com.studyolle.WithAccount;
import com.studyolle.WithAccountSecurityContextFactory;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.form.SignUpForm;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.StudyAccount;
import com.studyolle.study.form.StudyForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudyControllerTest {

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyService studyService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void clear() {
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("스터디 폼 보여주기")
    void createFormTest() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"));
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("스터디 개설 - 성공")
    void createStudySuccessTest() throws Exception {
        // given
        mockMvc.perform(post("/new-study")
                    .param("path", "study")
                    .param("title", "새로운 스터디")
                    .param("shortDescription", "짧은소개")
                    .param("fullDescription", "긴 소개")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study"));

        // when
        Study study = studyRepository.findByPath("study");

        // then
        assertThat(study).isNotNull();
        assertThat(study.getPath()).isEqualTo("study");
        assertThat(study.getTitle()).isEqualTo("새로운 스터디");
        assertThat(study.getShortDescription()).isEqualTo("짧은소개");
        assertThat(study.getFullDescription()).isEqualTo("긴 소개");
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("스터디 개설 - 실패")
    void createStudyFailTest() throws Exception {
        // given
        mockMvc.perform(post("/new-study")
                .param("path", "study----asfasnfsafnsafnasfnasklfafafafsfsaffnalkanslkfnsalkfnsalk" +
                        "fnsalkfsanflknsalkfnsaflknsalkfnsalkfnsalkfnlksanflksanflksanflksa")
                .param("title", "새로운 스터디fnaskflnaslfnkansflnkalffn")
                .param("shortDescription", "짧은소개")
                .param("fullDescription", "긴 소개")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("스터디 뷰 보여주기")
    void viewStudyTest() throws Exception {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("Short");
        studyForm.setFullDescription("full");

        studyService.createNewStudy(account, studyForm);

        mockMvc.perform(get("/study/study"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("스터디 회원조회")
    void showMembersTest() throws Exception {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("Short");
        studyForm.setFullDescription("full");

        studyService.createNewStudy(account, studyForm);

        mockMvc.perform(get("/study/study/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/members"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test @DisplayName("스터디 가입 테스트")
    @WithAccount("youngbin")
    void joinStudyTest() throws Exception {
        Account account = createByStudyManager();
        createByStudy(account);

        mockMvc.perform(get("/study/study/join"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study"));

        Study study = studyRepository.findStudyAccountsByPathAndManager("study", false);

        assertThat(study).isNotNull();
        assertThat(study.getStudyAccounts().size()).isEqualTo(1);
        assertThat(study.getStudyAccounts().get(0).getAccount().getNickname()).isEqualTo("youngbin");
        assertThat(study.getStudyAccounts().get(0).isManager()).isFalse();
    }

    @Test @DisplayName("스터디 탈퇴 테스트")
    @WithAccount("youngbin")
    void leaveStudyTest() throws Exception {
        Account account = createByStudyManager();
        Study newStudy = createByStudy(account);

        Account newAccount = accountRepository.findByNickname("youngbin");

        studyService.joinStudy(newStudy, newAccount);

        mockMvc.perform(get("/study/study/leave"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study"));

        Study findStudy = studyRepository.findStudyAccountsByPath("study");

        assertThat(findStudy).isNotNull();
        assertThat(findStudy.getStudyAccounts().size()).isEqualTo(1);
    }

    Account createByStudyManager() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("newStudyManager");
        signUpForm.setEmail("sss@gmail.com");
        signUpForm.setPassword("123456789");

        return accountService.processNewAccount(signUpForm);
    }

    Study createByStudy(Account account) {
        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        return studyService.createNewStudy(account, studyForm);
    }
}