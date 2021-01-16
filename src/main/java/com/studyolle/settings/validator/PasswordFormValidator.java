package com.studyolle.settings.validator;

import com.studyolle.settings.form.PasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

//TODO 2021.01.17 28. 패스워드 수정
//     1. 패스워드 수정할때 새 비밀번호와 새 비밀번호 확인의 값이 동일하지 않음을
//        검증하는 Validator 등록 및 에러 메시지 설정
//     2. 의존성을 주입받지 않음으로 @Component 선언하지 않음 - 스프링 빈으로 등록하지 않음
public class PasswordFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return PasswordForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        PasswordForm passwordForm = (PasswordForm)object;
        if(!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
            errors.rejectValue("newPassword", "wrong.value", "입력한 새 패스워드가 일치하지 않습니다.");
        }
    }
}
