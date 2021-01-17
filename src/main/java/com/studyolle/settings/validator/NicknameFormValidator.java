package com.studyolle.settings.validator;

import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import com.studyolle.settings.form.NicknameForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameFormValidator implements Validator {
    private final AccountRepository accountRepository;


    @Override
    public boolean supports(Class<?> aClass) {
        return NicknameForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm)object;
        Account findAccount = accountRepository.findByNickname(nicknameForm.getNickname());
        if (findAccount != null) {
            errors.rejectValue("nickname", "wrong.value", "입력하신 닉네임을 사용할 수 없습니다.");
        }
    }
}
