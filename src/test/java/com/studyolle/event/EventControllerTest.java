package com.studyolle.event;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.study.StudyRepository;
import com.studyolle.study.StudyService;
import com.studyolle.study.form.StudyForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    StudyService studyService;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void clear() {
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test @DisplayName("모임 만들기 폼 보여주기")
    @WithAccount("youngbin")
    void createEventFormTest() throws Exception {
        createByStudy();

        mockMvc.perform(get("/study/study/new-event"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("eventForm"));
    }

    Study createByStudy() {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setTitle("title");
        studyForm.setPath("study");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        return studyService.createNewStudy(account, studyForm);
    }
}