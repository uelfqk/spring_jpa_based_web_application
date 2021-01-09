package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

//TODO 2021.01.10
//     Spring Data Jpa 에서 findByEmail(String email); 을 정의해서 테스트 중
//     no default constructor Exception 이 발생하여 엔티티 클래스 구조 변경
//     @Builder 제거 -> @NoArgsConstructor(access = AccessLevel.PROTECTED) 추가
//     public static Account createAccount(String nickname, String email, String password) 메소드 추가

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//TODO 연관관계가 복잡해질때 이퀄즈 해쉬코드에서 서로다른 연관관계를 순환 참조할수 있어 스택오버플로우가 발생할 수 있다.
@EqualsAndHashCode(of = "id")
@ToString
public class Account {
    @Id @GeneratedValue
    private Long id;

    //TODO 로그인에 필요한 정보 ( 아이디 )
    @Column(unique = true)
    private String email;

    //TODO 로그인에 필요한 정보 ( 아이디 )
    @Column(unique = true)
    private String nickname;

    //TODO 로그인에 필요한 정보 ( 패스워드 )
    private String password;

    //TODO 이메일 인증절차 - 해당 계정이 이메일 인증이 된 계정인지 확인
    private boolean emailVerified;

    //TODO 이메일을 검증할 때 사용할 토큰 - 디비에 저장해두고 매치하는지 확인
    private String emailCheckToken;

    //TODO 이메일 인증을 거친 계정에 대해서 joinedAt 날짜로 가입날짜 지정
    private LocalDateTime joinedAt;

    //TODO 프로필 정보 ( 자개소개 )
    private String bio;

    //TODO 웹사이트 URL
    private String url;

    //TODO 직업
    private String occupation;

    //TODO 주거지
    private String location;

    //TODO 프로필 이미지 - 유저를 로딩할때 거의 같이 사용할 것이라 EAGER 설정 -> 나중에 LAZY 로 변경해서 해보기
    @Lob @Basic(fetch = FetchType.LAZY)
    private String profileImage;

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

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public static Account createAccount(String nickname, String email, String password) {
        Account account = new Account();
        account.setNickname(nickname);
        account.setEmail(email);
        account.setPassword(password);
        account.setStudyEnrollmentResultByWeb(true);
        account.setStudyCreatedByWeb(true);
        account.setStudyUpdatedByWeb(true);
        return account;
    }
}
