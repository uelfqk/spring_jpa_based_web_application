package com.studyolle.settings;

import com.studyolle.account.AccountService;
import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.settings.form.Notifications;
import com.studyolle.settings.form.PasswordForm;
import com.studyolle.settings.form.Profile;
import com.studyolle.settings.validator.PasswordFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingController {

    private final AccountService accountService;

    //TODO 2021.01.17 28. 패스워드 수정
    //     1. 비밀번호 검증 Validator 를 WebDataBinder 에 등록
    //     2. 스프링 빈으로 등록하지 않았음으로 new operation 을 이용해 등록
    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    //TODO 2021.01.16 25.프로필 수정
    //     1. 프로필 수정화면으로 이동 요청을 처리하는 핸들러
    //     2. 인증된 유저의 정보 @CurrentUser Account account 와
    //        뷰에서 사용할 Form 데이터에 유저정보를 저장하여 전달
    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute("profile", new Profile(account));
        return "settings/profile";
    }

    //TODO 2021.01.16 25.프로필 수정
    //     1. 프로필 정보 수정 요청을 처리하는 핸들러
    //     2. 폼에서 넘어오는 데이터를 validation 할때는 반드시
    //        @Valid  @ModelAttribute Profile profile, Errors errors 순서로 매개변수 정의
    //     3. 수정된 정보를 데이터베이스에 업데이트하는 행위는 AccountService 에 위임
    //     4. POST 요청은 반드시 리다이랙트 처리
    @PostMapping("/settings/profile")
    public String updateProfile(@Valid @ModelAttribute Profile profile, Errors errors,
                                @CurrentUser Account account, Model model,
                                RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            return "settings/profile";
        }

        accountService.updateProfile(account, profile);
        //TODO 2021.01.16 25.프로필 수정
        //     1. 리다이랙트 시킬때 한번쓰고 더이상 사용하지 않는 데이터 - Model 에 자동으로 들어가게된다.
        //      1). attributes.addFlashAttribute("message", "프로필을 수정하였습니다.");
        attributes.addFlashAttribute("message", "프로필을 수정하였습니다.");
        return "redirect:/" + "settings/profile";
    }

    @GetMapping("/settings/password")
    public String updatePasswordForm(@CurrentUser Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute("passwordForm", new PasswordForm());
        return "settings/password";
    }

    //TODO 2021.01.17 28. 패스워드 수정
    @PostMapping("/settings/password")
    public String updatePassword(@Valid @ModelAttribute PasswordForm passwordForm, Errors errors,
                                 @CurrentUser Account account, Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute("account", account);
            return "settings/password";
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "비밀번호를 변경했습니다.");
        return "redirect:/" + "settings/password";
    }

    //TODO 2021.01.17 28. 패스워드 수정
    //     1. 강의 외 개별 검증로직 구현
    @PostMapping("/settings/password/mylogic")
    public String updatePasswordMyLogic(@Valid @ModelAttribute PasswordForm passwordForm, Errors errors,
                                 @CurrentUser Account account, Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute("account", account);
            return "settings/password";
        }
        //TODO 2021.01.17 28. 패스워드 수정
        //     1. 입력한 새 비밀번호가 일치하지 않음을 검증하는 로직을 PasswordForm 객체에 위임하여 코드 작성
        //     2. 해당 검증 로직은 Validator 로 구현하는 것이 더 좋은 코드로 판단
        if(!passwordForm.isEqualsPassword()) {
            model.addAttribute("account", account);
            model.addAttribute("message", "비밀번호가 일치하지 않습니다.");
            return "settings/password";
        }

        //TODO 2021.01.17 28. 패스워드 수정
        //     1. 현재 사용중인 비밀번호와 새로운 비밀번호가 동일하면 비밀번호 변경이 되지 않도록 구현
        //        AccountService Layer 에는 현재 비밀번호와 새로운 비밀번호에 대한 데이터를 모두 받을 수 있음으로
        //        해당 서비스에 위임하여 처리하는것이 옳다고 판단
        //     2. 해당 검증로직을 Validator 로 구현하고자하면 현재 유저의 패스워드를 얻어와야하는데 구현방법 생각 중
        if(!accountService.updatePasswordMyLogic(account, passwordForm.getNewPassword())) {
            model.addAttribute("account", account);
            model.addAttribute("message", "기존 비밀번호와 동일하게 변경할 수 없습니다.");
            return "settings/password";
        }

        attributes.addFlashAttribute("message", "비밀번호를 변경했습니다.");
        return "redirect:/" + "settings/password";
    }

    //TODO 2021.01.17 30.알림 설정
    @GetMapping("/settings/notifications")
    public String updateNotificationsForm(@CurrentUser Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute("notifications", new Notifications(account));
        return "settings/notifications";
    }

    //TODO 2021.01.17 30.알림 설정
    @PostMapping("/settings/notifications")
    public String updateNotifications(@Valid @ModelAttribute Notifications notifications, Errors errors,
                                      @CurrentUser Account account, Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute("account", account);
            return "settings/notifications";
        }

        accountService.updateNotifications(account, notifications);
        attributes.addFlashAttribute("message", "알림 설정이 수정되었습니다.");
        return "redirect:/" + "settings/notifications";
    }
}
