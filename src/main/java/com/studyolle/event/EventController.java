package com.studyolle.event;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import com.studyolle.event.form.EventForm;
import com.studyolle.event.validator.EventFormValidator;
import com.studyolle.study.StudyRepository;
import com.studyolle.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
public class EventController {
    private final StudyRepository studyRepository;
    private final EventService eventService;
    private final EventFormValidator eventFormValidator;
    private final EventRepository eventRepository;
    private final StudyService studyService;

    private final ModelMapper modelMapper;

    @InitBinder(value = "eventForm")
    public void eventFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventFormValidator);
    }

    //TODO 2021.02.20 62. 모임 만들기
    //                 1. 모임 만들기 폼에 필요한 데이터를 랜더링하여 클라이언트로 반환
    @GetMapping("/new-event")
    public String createEventForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyWithManager(account, path);

        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("eventForm", new EventForm());

        return "event/form";
    }

    //TODO 2021.02.21 63. 모임 만들기 폼 서브밋
    //                 1. 폼에서 입력받는 각 날짜는 상관관계를 가짐으로 별도의 Validator 를 만들어
    //                    검증 필요
    //                 2. event/validator/EventFormValidator.java
    @PostMapping("/new-event")
    public String createEvent(@CurrentUser Account account, @PathVariable String path, Model model,
                              @Valid @ModelAttribute EventForm eventForm, Errors errors) throws UnsupportedEncodingException {
        Study study = studyService.getStudyWithManager(account, path);

        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            return "event/form";
        }

        Event event = eventService.createNewEvent(account, study, eventForm);

        return "redirect:/study/" + study.getEncodingPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{event-id}")
    public String showEvents(@CurrentUser Account account, @PathVariable String path,
                             @PathVariable(value = "event-id") Long eventId, Model model) {
        Study study = studyRepository.findStudyAccountsByPath(path);
        Event event = eventRepository.findWithCreateByWithEnrollmentsById(eventId);

        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("event", event);

        return "event/view";
    }

    @GetMapping("/events/{event-id}/edit")
    public String editViewEvents(@CurrentUser Account account, @PathVariable String path,
                                 @PathVariable(value = "event-id") Long eventId, Model model) {
        Study study = studyService.getStudyWithManager(account, path);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 정보입니다."));

        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("event", event);
        model.addAttribute("eventForm", modelMapper.map(event, EventForm.class));

        return "event/update-form";
    }

    @PostMapping("/events/{event-id}/edit")
    public String editEvents(@CurrentUser Account account, @PathVariable String path,
                             @PathVariable(value = "event-id") Long eventId,
                             @Valid @ModelAttribute EventForm eventForm, Errors errors, Model model)
            throws UnsupportedEncodingException {
        Study study = studyService.getStudyWithManager(account, path);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("모임 정보가 정화하지 않습니다."));

        eventFormValidator.validateUpdateForm(eventForm, event, errors);

        if(errors.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            model.addAttribute("event", event);
            return "event/view";
        }

        eventService.editEvent(event, eventForm);

        return "redirect:/study/" + study.getEncodingPath() + "/events/" + event.getId();
    }

    @PostMapping("/events/{event-id}/enroll")
    public String enrollmentEvent(@CurrentUser Account account, @PathVariable String path,
                              @PathVariable(value = "event-id") Long eventId) throws UnsupportedEncodingException {

        Event event = eventRepository.findWithStudyWithEnrollmentsById(eventId);

        eventService.enrollmentEvent(event, account);
        return "redirect:/study/" + event.getStudy().getEncodingPath() + "/events/" + event.getId();
    }

    @PostMapping("/events/{event-id}/disenroll")
    public String disEnrollmentEvent(@CurrentUser Account account, @PathVariable String path,
                                     @PathVariable(value = "event-id") Long eventId) throws UnsupportedEncodingException {

        Study study = studyService.getStudyToUpdate(account, path);

        Event event = eventRepository.findWithEnrollmentsById(eventId);

        eventService.disEnrollmentEvent(event, account);

        return "redirect:/study/" + study.getEncodingPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{event-id}/enrollments/{enroll-id}/accept")
    public String acceptEnrollmentAccount(@CurrentUser Account account,
                                          @PathVariable String path,
                                          @PathVariable(value = "event-id") Long eventId,
                                          @PathVariable(value = "enroll-id") Long enrollmentId) throws UnsupportedEncodingException {

        Study study = studyService.getStudyWithManager(account, path);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        eventService.acceptedEnrollAccount(event, enrollmentId);

        return "redirect:/study/" + study.getEncodingPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{event-id}/enrollments/{enroll-id}/reject")
    public String rejectedEnrollmentAccount(@CurrentUser Account account,
                                            @PathVariable String path,
                                            @PathVariable(value = "event-id") Long eventId,
                                            @PathVariable(value = "enroll-id") Long enrollmentId) throws UnsupportedEncodingException {

        Study study = studyService.getStudyWithManager(account, path);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        eventService.rejectedEnrollAccount(event, enrollmentId);

        return "redirect:/study/" + study.getEncodingPath() + "/events/" + event.getId();
    }


    @GetMapping("/events")
    public String showEvents(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyRepository.findByPath(path);

        List<Event> events = eventRepository.findAllByStudyId(study.getId());

        List<Event> oldEvents = events.stream().filter(e -> e.getEndDateTime().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        List<Event> newEvents = events.stream().filter(e -> e.getEndDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("oldEvents", oldEvents);
        model.addAttribute("newEvents", newEvents);
        return "study/events";
    }

    @GetMapping("/events/{event-id}/enrollments/{enroll-id}/checkin")
    public String checkInEvent(@CurrentUser Account account,
                               @PathVariable String path,
                               @PathVariable("event-id") Long eventId,
                               @PathVariable("enroll-id") Long enrollmentId) throws UnsupportedEncodingException {

        Study study = studyService.getStudyToUpdate(account, path);

        Event event = getEventOnly(eventId);

        eventService.checkInEvent(event, enrollmentId);

        return "redirect:/study/" + study.getEncodingPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{event-id}/enrollments/{enroll-id}/cancel-checkin")
    public String checkOutEvent(@CurrentUser Account account,
                                @PathVariable String path,
                                @PathVariable("event-id") Long eventId,
                                @PathVariable("enroll-id") Long enrollmentId) throws UnsupportedEncodingException {
        Study study = studyService.getStudyToUpdate(account, path);

        Event event = getEventOnly(eventId);

        eventService.cancelCheckInEvent(event, enrollmentId);

        return "redirect:/study/" + study.getEncodingPath() + "/events/" + event.getId();
    }

    private Event getEventOnly(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(""));
    }

}
