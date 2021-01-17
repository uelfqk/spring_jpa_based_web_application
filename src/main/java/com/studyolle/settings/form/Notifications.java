package com.studyolle.settings.form;

import com.studyolle.domain.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class Notifications {
    //TODO 스터디 생성 결과를 이메일로 받을지 여부
    private boolean studyCreatedByEmail;

    //TODO 스터디 생성 결과를 웹으로 받을지 여부
    private boolean studyCreatedByWeb;

    //TODO 스터디 가입 여부를 이메일로 받을지 여부
    private boolean studyEnrollmentResultByEmail;

    //TODO 스터디 가입여부를 웹으로 받을지 여부
    private boolean studyEnrollmentResultByWeb;

    //TODO 스터디 갱신정보를 이메일로 받을지 여부
    private boolean studyUpdatedByEmail;

    //TODO 스터디 갱신정보를 웹으로 받을지 여부
    private boolean studyUpdatedByWeb;

    public Notifications(Account account) {
        this.studyCreatedByEmail = account.isStudyCreatedByEmail();
        this.studyCreatedByWeb = account.isStudyUpdatedByWeb();
        this.studyEnrollmentResultByEmail = account.isStudyEnrollmentResultByEmail();
        this.studyEnrollmentResultByWeb = account.isStudyEnrollmentResultByWeb();
        this.studyUpdatedByEmail = account.isStudyUpdatedByEmail();
        this.studyUpdatedByWeb = account.isStudyUpdatedByWeb();
    }
}
