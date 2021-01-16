package com.studyolle.account;

import com.studyolle.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignUpForm.class);
    }

    //TODO 커스텀 Validation
    @Override
    //TODO object : 실제로 폼에서 들어오는 객체,
    //     errors : 해당 객체로 바인딩할때 에러를 담는 객체
    public void validate(Object object, Errors errors) {
        //TODO email, nickname 중복 검사
        SignUpForm signUpForm = (SignUpForm)object;

        if(accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이메일");
        }

        if(accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임입니다.");
        }
    }
}
