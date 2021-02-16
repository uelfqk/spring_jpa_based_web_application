package com.studyolle.study;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.StudyManager;
import com.studyolle.study.form.StudyDescriptionForm;
import com.studyolle.study.form.StudyForm;
import com.studyolle.study.form.StudyMembersDto;
import com.studyolle.study.validator.StudyFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {
    private final StudyService studyService;
    private final StudyFormValidator studyFormValidator;
    private final StudyRepository studyRepository;

    @InitBinder("studyForm")
    public void studyFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyFormValidator);
    }

    @GetMapping("/new-study")
    public String createForm(@CurrentUser Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute("studyForm", new StudyForm());
        return "study/form";
    }

    @PostMapping("/new-study")
    public String createStudy(@Valid @ModelAttribute StudyForm studyForm, Errors errors,
                              @CurrentUser Account account, Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute("account", account);
            return "study/form";
        }

        Study newStudy = studyService.createNewStudy(account, studyForm);
        return "redirect:/study/" + newStudy.getPath();// + URLEncoder.encode(newStudy.getPath(), "UTF-8");
    }

    @GetMapping("/study/{path}")
    public String viewStudy(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);

        model.addAttribute("account", account);
        model.addAttribute("study", study);

        return "study/view";
    }

    @GetMapping("/study/{path}/members")
    public String showMembers(@CurrentUser Account account, @PathVariable String path , Model model) {
        Study study = studyService.findMembers(path);

        model.addAttribute("account", account);
        model.addAttribute("study", study);

        return "study/members";
    }

    @GetMapping("/study/{path}/join")
    public String joinStudy(@CurrentUser Account account, @PathVariable String path) throws UnsupportedEncodingException {
        Study study = studyRepository.findStudyAccountsByPath(path);
        studyService.joinStudy(study, account);
        return "redirect:/study/" + study.getEncodingPath();
    }

    @GetMapping("/study/{path}/leave")
    public String leaveStudy(@CurrentUser Account account, @PathVariable String path) throws UnsupportedEncodingException {
        Study study = studyRepository.findStudyAccountsByPath(path);
        studyService.leaveStudy(study, account);
        return "redirect:/study/" + study.getEncodingPath();
    }
}
