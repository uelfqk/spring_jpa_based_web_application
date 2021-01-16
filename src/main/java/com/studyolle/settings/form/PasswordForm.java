package com.studyolle.settings.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@NoArgsConstructor
public class PasswordForm {

    @NotBlank
    @Length(min = 8, max = 50)
    private String newPassword;

    @NotBlank
    @Length(min = 8, max = 50)
    private String newPasswordConfirm;

    public boolean isEqualsPassword() {
        return newPassword.equals(newPasswordConfirm);
    }
}
