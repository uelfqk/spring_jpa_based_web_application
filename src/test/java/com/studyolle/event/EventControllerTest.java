package com.studyolle.event;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.form.SignUpForm;
import com.studyolle.domain.Account;
import com.studyolle.domain.Enrollment;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import com.studyolle.enums.EventType;
import com.studyolle.event.form.EventForm;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    StudyService studyService;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    EventService eventService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    EntityManager em;

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

    @Test @DisplayName("모임 뷰 보여주기")
    @WithAccount("youngbin")
    void showEventTest() throws Exception {
        Account account = findAccount();
        Study study = createByStudy(account);
        Event event = createByEvent(account, study, EventType.FCFS);
        MvcResult result = mockMvc.perform(get("/study/study/events/" + event.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("event/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("event"))
                .andReturn();

        Event requestEvent = (Event)result.getRequest().getAttribute("event");

        assertThat(requestEvent).isEqualTo(event);
    }

    @Test @DisplayName("모임 수정 폼 보여주기")
    @WithAccount("youngbin")
    void editViewEventTest() throws Exception {
        Account account = findAccount();
        Study study = createByStudy(account);
        Event event = createByEvent(account, study, EventType.FCFS);

        MvcResult result = mockMvc.perform(get("/study/study/events/" + event.getId() + "/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/update-form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("eventForm"))
                .andReturn();

        EventForm eventForm = (EventForm)result.getRequest().getAttribute("eventForm");

        assertThat(event.getTitle()).isEqualTo(eventForm.getTitle());
        assertThat(event.getDescription()).isEqualTo(eventForm.getDescription());
        assertThat(event.getLimitOfEnrollments()).isEqualTo(eventForm.getLimitOfEnrollments());
        assertThat(event.getEndEnrollmentDateTime()).isEqualTo(eventForm.getEndEnrollmentDateTime());
        assertThat(event.getStartDateTime()).isEqualTo(eventForm.getStartDateTime());
        assertThat(event.getEndDateTime()).isEqualTo(eventForm.getEndDateTime());
        assertThat(event.getEventType()).isEqualTo(eventForm.getEventType());
    }


    @Test @DisplayName("모임 수정하기 - 성공")
    @WithAccount("youngbin")
    void editEventSuccessTest() throws Exception {
        Account account = findAccount();
        Study study = createByStudy(account);
        Event event = createByEvent(account, study, EventType.FCFS);

        MvcResult result = mockMvc.perform(post("/study/study/events/" + event.getId() + "/edit")
                .param("title", "newEventTitle")
                .param("description", "newEventDescription")
                .param("eventType", EventType.CONFIRMATIVE.toString())
                .param("limitOfEnrollments", "4")
                .param("endEnrollmentDateTime", LocalDateTime.now().plusHours(2L).toString())
                .param("startDateTime", LocalDateTime.now().plusHours(4L).toString())
                .param("endDateTime", LocalDateTime.now().plusHours(6L).toString())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study/events/" + event.getId()))
                .andExpect(model().hasNoErrors())
                .andReturn();

        Event findEvent = eventRepository.findById(event.getId())
                .orElseThrow(() -> new IllegalArgumentException(""));

        assertThat(findEvent).isNotNull();
        assertThat(findEvent.getTitle()).isEqualTo("newEventTitle");
        assertThat(findEvent.getDescription()).isEqualTo("newEventDescription");
        assertThat(findEvent.getEventType()).isEqualTo(EventType.CONFIRMATIVE);
        assertThat(findEvent.getLimitOfEnrollments()).isEqualTo(4);
    }

    @Test @DisplayName("선착순 모임 참가하기")
    @WithAccount("youngbin")
    void enrollmentEventTest() throws Exception {
        Account newAccount = createNewAccount("newAccount");
        Study study = createByStudy(newAccount);
        Event event = createByEvent(newAccount, study, EventType.FCFS);

        System.out.println("/study/" + study.getEncodingPath() + "/events/" + event.getId() + "/enroll");

        mockMvc.perform(post("/study/" + study.getEncodingPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getEncodingPath() + "/events/" + event.getId()));

        Event findEvent = eventRepository.findWithStudyWithEnrollmentsById(event.getId());

        assertThat(findEvent).isNotNull();
        assertThat(findEvent.getEnrollments().size()).isEqualTo(1);
        assertThat(findEvent.getEnrollments().get(0).getAccount()).isEqualTo(findAccount());
    }

    @Test @DisplayName("선착순 모임 취소하기")
    @WithAccount("youngbin")
    @Transactional
    void disEnrollmentEventTest() throws Exception {
        Account newAccount = createNewAccount("newAccount");
        Study study = createByStudy(newAccount);
        Event event = createByEvent(newAccount, study, EventType.FCFS);

        Account account = findAccount();

        eventService.enrollmentEvent(event, account);

        mockMvc.perform(post("/study/" + study.getEncodingPath() + "/events/" + event.getId() +"/disenroll")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getEncodingPath() + "/events/" + event.getId()));

        Event findEvent = eventRepository.findWithStudyWithEnrollmentsById(event.getId());

        assertThat(findEvent).isNotNull();
        assertThat(findEvent.getEnrollments().size()).isEqualTo(0);
    }

    @Test @DisplayName("모임 참가 - 관리자 확인 - 수락")
    @WithAccount("youngbin")
    @Transactional
    void acceptEventTest() throws Exception {
        Account account = findAccount("youngbin");

        Study study = createByStudy(account);
        Event event = createByEvent(account, study, EventType.CONFIRMATIVE);

        Account newAccount = createNewAccount("newAccount");

        eventService.enrollmentEvent(event, newAccount);

        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, newAccount);

        mockMvc.perform(get("/study/" + study.getEncodingPath() + "/events/" + event.getId() + "/enrollments/" + enrollment.getId() + "/accept"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getEncodingPath() + "/events/" + event.getId()));

        em.flush();
        em.clear();

        Enrollment findEnrollment = enrollmentRepository.findByEventAndId(event, enrollment.getId());

        assertThat(findEnrollment).isNotNull();
        assertThat(findEnrollment.isAccepted()).isTrue();
    }

    @Test @DisplayName("모임 참가 - 관리자 확인 - 취소")
    @WithAccount("youngbin")
    @Transactional
    void rejectedEnrollmentAccountTest() throws Exception {
        Account account = findAccount("youngbin");

        Study study = createByStudy(account);
        Event event = createByEvent(account, study, EventType.CONFIRMATIVE);

        Account newAccount = createNewAccount("newAccount");

        eventService.enrollmentEvent(event, newAccount);

        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, newAccount);

        eventService.acceptedEnrollAccount(event, enrollment.getId());

        mockMvc.perform(get("/study/" + study.getEncodingPath() + "/events/" + event.getId() + "/enrollments/" + enrollment.getId() + "/reject"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getEncodingPath() + "/events/" + event.getId()));

        em.flush();
        em.clear();

        Enrollment findEnrollment = enrollmentRepository.findByEventAndAccount(event, newAccount);

        assertThat(findEnrollment.isAccepted()).isFalse();

    }

    Long getEventIdToLong(String eventId) {
        return Long.parseLong(eventId);
    }

    Account findAccount() {
        return accountRepository.findByNickname("youngbin");
    }

    Account findAccount(String username) {
        return accountRepository.findByNickname(username);
    }

    Study createByStudy(Account account) {

        StudyForm studyForm = new StudyForm();
        studyForm.setTitle("title");
        studyForm.setPath("study");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        return studyService.createNewStudy(account, studyForm);
    }

    Event createByEvent(Account account, Study study, EventType eventType) {
        EventForm eventForm = new EventForm();
        eventForm.setTitle("eventTitle");
        eventForm.setDescription("eventDescription");
        eventForm.setLimitOfEnrollments(2);
        eventForm.setEventType(eventType);
        eventForm.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        eventForm.setStartDateTime(LocalDateTime.now().plusDays(2));
        eventForm.setEndDateTime(LocalDateTime.now().plusDays(20));
        return eventService.createNewEvent(account, study, eventForm);
    }

    Account createNewAccount(String nickname) {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname(nickname);
        signUpForm.setPassword("123456789");
        signUpForm.setEmail("aaa@aaa.co.kr");

        return accountService.processNewAccount(signUpForm);
    }
}