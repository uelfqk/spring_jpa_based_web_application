package com.studyolle.settings.form;

import com.studyolle.domain.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
//@NoArgsConstructor
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

    //TODO 2021.01.17 31.ModelMapper 적용
    //     1. Notifications 객체는 스프링 빈이 아니기 때문에 ModelMapper 를 주입 받을 수 없는 상황
    //     2. 객체의 생성과 프로퍼티 매핑을 해당 객체에서 하지 않고 Controller Layer 로 위임
    //     3. 해당 생성자 제거, 해당 생성자를 제거함으로 기본 생성자가 활성화 됨으로 @NoArgsConstructor 제거 
    //     4. Code
    // ------------------------------------------------------------------------------------
    // public Notifications(Account account) {
    //     this.studyCreatedByEmail = account.isStudyCreatedByEmail();
    //     this.studyCreatedByWeb = account.isStudyUpdatedByWeb();
    //     this.studyEnrollmentResultByEmail = account.isStudyEnrollmentResultByEmail();
    //     this.studyEnrollmentResultByWeb = account.isStudyEnrollmentResultByWeb();
    //     this.studyUpdatedByEmail = account.isStudyUpdatedByEmail();
    //     this.studyUpdatedByWeb = account.isStudyUpdatedByWeb();
    // }
}
