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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;

    private final AccountRepository accountRepository;

    //TODO 2021.01.08 - 6.회원 가입 폼 서브밋 검증
    //     InitBinder 를 이용하여 value 로 지정한 이름에 해당하는 객체를
    //     해당 객체를 받을때 303 검증 코드도 실행이 되고 Custom Validator 로 지정한 검증도 수행한다.
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    //TODO 2021.01.08 - 6.회원 가입 폼 서브밋 검증
    @GetMapping("/sign-up")
    public String newFrom(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up";
    }

    //TODO 2021.01.08 - 7.회원 가입 폼 서브밋 처리
    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors) {
        //TODO JSR303 검증
        if (errors.hasErrors()) {
            return "account/sign-up";
        }

        accountService.processNewAccount(signUpForm);

        //TODO 회원 가입 처리
        return "redirect:/";
    }

    //TODO 2021.01.10 - 10.회원가입 인증 메일 확인
    //     이메일 인증처리에 사용 메소드
    //     GET http://localhost:8080/check-email-token?token=${token}&email=${email}
    //     웹브라우저에서 전달되는 url 처리
    //     @RequestParam - 생략 가능
    @GetMapping("/check-email-token")
    public String checkEmailToken(@RequestParam String token,
                                  @RequestParam String email,
                                  Model model) {
        Account account = accountRepository.findByEmail(email);
        String viewName = "account/checked-email";

        //TODO 이메일로 조회하기때문에 해당 엔티티가 null 이면 이메일이 잘못되었다는 의미
        if(account == null) {
            model.addAttribute("error", "wrong.email");
            return viewName;
        }

        //TODO 이메일을 발송할때 발행한 토큰과 브라우저에서 전송한 토큰이 다르면
        //     토큰이 잘못되었다는 의미
        if(account.getEmailCheckToken().equals(token) == false) {
            model.addAttribute("error", "wrong.token");
            return viewName;
        }

        account.completeSignUp();

        //TODO View 에 출력할 내용을 model 에 담아 전달
        //     이메일을 확인했습니다. *{n} 번째 회원, *{nickname} 님 가입을 축하합니다.
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return viewName;
    }
}
