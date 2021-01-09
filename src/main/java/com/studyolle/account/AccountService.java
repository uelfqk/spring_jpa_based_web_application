package com.studyolle.account;

import com.studyolle.account.dto.SignUpForm;
import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    //TODO 2021.01.09 - 8.회원가입 리팩토링 및 테스트
    //     기존 Controller 에서 사용하는 의존성을 Service Layer 로 이동
    //     Controller 의 의존성을 AccountService 만을 받게 변경
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    //TODO 2021.01.09 - 8.회원가입 리팩토링 및 테스트
    //     Controller Layer 에서 사용할 메소드만을 public 접근제어자로 공개
    public void processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        //TODO 이메일 전송에 필요한 토큰 발행
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
    }

    //TODO 2021.01.09 - 8.회원가입 리팩토링 및 테스트
    //     리팩토링 : 회원가입을 담당하는 기능을 추출하여 메소드로 분리
    //     Controller Layer 에서 알고있지 않아도 됨으로 private 접근제 어자로 비공개
    private Account saveNewAccount(SignUpForm signUpForm) {
//        Account account = Account.builder()
//                .email(signUpForm.getEmail())
//                .nickname(signUpForm.getNickname())
//                .password(passwordEncoder.encode(signUpForm.getPassword())) //TODO encoding 해야함 - 해쉬로 변경해서 저장
//                .studyEnrollmentResultByWeb(true)
//                .studyUpdatedByWeb(true)
//                .studyEnrollmentResultByWeb(true)
//                .build();

        Account account = Account.createAccount(signUpForm.getNickname(), signUpForm.getEmail(),
                passwordEncoder.encode(signUpForm.getPassword()));

        //TODO 뷰에서 받은 데이터 저장
        Account newAccount = accountRepository.save(account);

        return newAccount;
    }

    //TODO 2021.01.09 - 8.회원가입 리팩토링 및 테스트
    //     리팩토링 : 이메일 전송을 담당하는 기능을 추출하여 메소드로 분리
    //     Controller Layer 에서 알고있지 않아도 됨으로 private 접근제어자로 비공개
    private void sendSignUpConfirmEmail(Account newAccount) {
        //TODO 이메일 전송
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail()); //TODO 이메일을 받을 사람
        mailMessage.setSubject("스터디올래, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
    }
}
