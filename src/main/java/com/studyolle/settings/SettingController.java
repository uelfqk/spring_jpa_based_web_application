package com.studyolle.settings;

import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.settings.dto.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingController {

    private final AccountService accountService;

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
}
