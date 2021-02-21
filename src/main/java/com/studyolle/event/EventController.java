package com.studyolle.event;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.event.form.EventForm;
import com.studyolle.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
public class EventController {

    private final StudyRepository studyRepository;

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
}
