package com.studyolle.event;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import com.studyolle.event.form.EventForm;
import com.studyolle.event.validator.EventFormValidator;
import com.studyolle.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
public class EventController {
    private final StudyRepository studyRepository;
    private final EventService eventService;
    private final EventFormValidator eventFormValidator;
    private final EventRepository eventRepository;

    @InitBinder(value = "eventForm")
    public void eventFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventFormValidator);
    }

    //TODO 2021.02.20 62. 모임 만들기
    //                 1. 모임 만들기 폼에 필요한 데이터를 랜더링하여 클라이언트로 반환
    @GetMapping("/new-event")
    public String createEventForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyRepository.findByPath(path);

        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("eventForm", new EventForm());

        return "event/form";
    }

    //TODO 2021.02.21 63. 모임 만들기 폼 서브밋
    //                 1. 폼에서 입력받는 각 날짜는 상관관계를 가짐으로 별도의 Validator 를 만들어
    //                    검증 필요
    //                 2. event/validator/EventFormValidator.java
    @PostMapping("/new-event/")
    public String createEvent(@CurrentUser Account account, @PathVariable String path, Model model,
                              @Valid @ModelAttribute EventForm eventForm, Errors errors) throws UnsupportedEncodingException {
        Study study = studyRepository.findByPath(path);

        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            return "event/form";
        }

        Event event = eventService.createNewEvent(account, study, eventForm);

        return "redirect:/study/" + study.getEncodingPath() + "/events/" + event.getId();
    }
}
