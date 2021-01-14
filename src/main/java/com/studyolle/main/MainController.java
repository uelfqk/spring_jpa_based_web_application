package com.studyolle.main;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    //TODO 2021.01.13 현재 인증된 사용자 정보 참조
    //     인증된 사용자 정보를 참조하여 첫페이지에서 인증된 사용자의 정보 출력
    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        //TODO account 가 null 이 아닌 경우 인증을 한 사용자 임으로 model 에 account 정보를 담아서 전달
        if(account != null) {
            model.addAttribute("account", account);
        }

        return "index";
    }

    //TODO 2021.01.14 로그인 / 로그아웃
    //     1. 로그인 페이지로 전환하는 핸들러
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
