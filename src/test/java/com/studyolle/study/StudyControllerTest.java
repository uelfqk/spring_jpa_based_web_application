package com.studyolle.study;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.study.form.StudyForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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
}