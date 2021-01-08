package com.studyolle.account.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class SignUpForm {
    @NotBlank
    @Length(min = 3, max = 20)
    //TODO 패턴 생성 방법 - ^ : 시작,
    //                   [] : valid 에 사용될 패턴 정의,
    //                   {3,20} 패턴에 사용할 글자의 수 정의 - 3: 최소값, 20 : 최대값
    //                   $ : 종료
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;
}
