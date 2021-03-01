package com.studyolle.event;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import com.studyolle.enums.EventType;
import com.studyolle.study.StudyRepository;
import com.studyolle.study.StudyService;
import com.studyolle.study.form.StudyForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
    EventService eventService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void clear() {
        eventRepository.deleteAll();
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test @DisplayName("모임 만들기 폼 보여주기")
    @WithAccount("youngbin")
    void createEventFormTest() throws Exception {
        createByStudy(findAccount());

        mockMvc.perform(get("/study/study/new-event"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("eventForm"));
    }

    @Test @DisplayName("모임 만들기 폼 서브밋 - 성공")
    @WithAccount("youngbin")
    void createEventSuccessTest() throws Exception {
        Account account = findAccount();
        Study newStudy = createByStudy(account);

        MvcResult result = mockMvc.perform(post("/study/study/new-event")
                .param("title", "eventTitle")
                .param("description", "eventDescription")
                .param("eventType", EventType.FCFS.toString())
                .param("limitOfEnrollments", "2")
                .param("endEnrollmentDateTime", LocalDateTime.now().plusHours(1).toString())
                .param("startDateTime", LocalDateTime.now().plusDays(3L).toString())
                .param("endDateTime", LocalDateTime.now().plusDays(5L).toString())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("account"))
                .andExpect(model().attributeDoesNotExist("study"))
                .andExpect(model().hasNoErrors())
                .andReturn();

        String redirectedUrl = result.getResponse().getRedirectedUrl();
        System.out.println("redirectedUrl = " + redirectedUrl);

        String[] split = redirectedUrl.split("/");

        Event event = eventRepository.findById(getEventIdToLong(split[4]))
                .orElseGet(() -> Event.defaultEvent());

        assertThat(event).isNotNull();
        assertThat(event.getTitle()).isEqualTo("eventTitle");
        assertThat(event.getDescription()).isEqualTo("eventDescription");
        assertThat(event.getEventType()).isEqualTo(EventType.FCFS);
        assertThat(event.getLimitOfEnrollments()).isEqualTo(2);
//        assertThat(event.getEndEnrollmentDateTime()).isEqualTo(LocalDateTime.now().plusHours(1));
//        assertThat(event.getStartDateTime()).isEqualTo(LocalDateTime.now().plusDays(3L));
//        assertThat(event.getEndDateTime()).isEqualTo(LocalDateTime.now().plusDays(5L));
    }

    @Test @DisplayName("모임 만들기 폼 서브밋 - 실패")
    @WithAccount("youngbin")
    void createEventFailTest() throws Exception {
        Account account = findAccount();
        Study newStudy = createByStudy(account);

        mockMvc.perform(post("/study/study/new-event")
                .param("title", "eventTitle")
                .param("description", "eventDescription")
                .param("eventType", EventType.FCFS.toString())
                .param("limitOfEnrollments", "2")
                .param("endEnrollmentDateTime", LocalDateTime.now().toString())
                .param("startDateTime", LocalDateTime.now().toString())
                .param("endDateTime", LocalDateTime.now().toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("event/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().hasErrors());
    }

    Long getEventIdToLong(String eventId) {
        return Long.parseLong(eventId);
    }

    Account findAccount() {
        return accountRepository.findByNickname("youngbin");
    }

    Study createByStudy(Account account) {

        StudyForm studyForm = new StudyForm();
        studyForm.setTitle("title");
        studyForm.setPath("study");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        return studyService.createNewStudy(account, studyForm);
    }

    Event createByEvent(Account account, Study study) {
        return Event.defaultEvent();
    }
}