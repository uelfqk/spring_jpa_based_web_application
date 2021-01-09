package com.studyolle.account;

import com.studyolle.account.dto.SignUpForm;
import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    //TODO InitBinder 를 이용하여 value 로 지정한 이름에 해당하는 객체를
    //     해당 객체를 받을때 303 검증 코드도 실행이 되고 Custom Validator 로 지정한 검증도 수행한다.
    // 2021.01.08 - 회원 가입 폼 서브밋 검증
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    //TODO 2021.01.08 - 회원 가입 폼 서브밋 검증
    @GetMapping("/sign-up")
    public String newFrom(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up";
    }

    //TODO 2021.01.08 - 회원 가입 폼 서브밋 처리
    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors) {
        //TODO JSR303 검증
        if (errors.hasErrors()) {
            return "account/sign-up";
        }

        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(signUpForm.getPassword()) //TODO encoding 해야함 - 해쉬로 변경해서 저장
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .build();

        //TODO 뷰에서 받은 데이터 저장
        Account newAccount = accountRepository.save(account);

        //TODO 이메일 전송에 필요한 토큰 발행
        newAccount.generateEmailCheckToken();

        //TODO 이메일 전송
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail()); //TODO 이메일을 받을 사람
        mailMessage.setSubject("스터디올래, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);

        //TODO 회원 가입 처리
        return "redirect:/";
    }
}
