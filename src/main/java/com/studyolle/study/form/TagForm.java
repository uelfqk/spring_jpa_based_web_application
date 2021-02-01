package com.studyolle.study.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class TagForm {
    @NotBlank
    private String tagTitle;
}
