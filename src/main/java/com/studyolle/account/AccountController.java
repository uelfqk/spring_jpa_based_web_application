package com.studyolle.account;

import com.studyolle.controller.dto.AccountDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {
    @GetMapping("/sign-up")
    public String signUp(Model model) {
        model.addAttribute("account", new AccountDto());
        return "account/sign-up";
    }
}
