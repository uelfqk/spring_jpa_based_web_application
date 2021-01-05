package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id") // 연관관계가 복잡해질때 이퀄즈 해쉬코드에서 서로다른 연관관계를 순환 참조할수 있어 스택오버플로우가 발생할 수 있다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id @GeneratedValue
    private Long id;

    // 로그인에 필요한 정보 ( 아이디 )
    @Column(unique = true)
    private String email;

    // 로그인에 필요한 정보 ( 아이디 )
    @Column(unique = true)
    private String nickname;

    // 로그인에 필요한 정보 ( 패스워드 )
    private String password;

    // 이메일 인증절차 - 해당 계정이 이메일 인증이 된 계정인지 확인
    private boolean emailVerified;

    // 이메일을 검증할 때 사용할 토큰 - 디비에 저장해두고 매치하는지 확인
    private String emailCheckToken;

    // 이메일 인증을 거친 계정에 대해서 joinedAt 날짜로 가입날짜 지정
    private LocalDateTime joinedAt;

    // 프로필 정보 ( 자개소개 )
    private String bio;

    // 웹사이트 URL
    private String url;

    // 직업
    private String occupation;

    // 주거지
    private String location;

    // 프로필 이미지 - 유저를 로딩할때 거의 같이 사용할 것이라 EAGER 설정 -> 나중에 LAZY 로 변경해서 해보기
    @Lob @Basic(fetch = FetchType.LAZY)
    private String profileImage;

    // 스터디 생성 결과를 이메일로 받을지 여부
    private boolean studyCreatedByEmail;

    // 스터디 생성 결과를 웹으로 받을지 여부
    private boolean studyCreatedByWeb;

    // 스터디 가입 여부를 이메일로 받을지 여부
    private boolean studyEnrollmentResultByEmail;

    // 스터디 가입여부를 웹으로 받을지 여부
    private boolean studyEnrollmentResultByWeb;

    // 스터디 갱신정보를 이메일로 받을지 여부
    private boolean studyUpdatedByEmail;

    // 스터디 갱신정보를 웹으로 받을지 여부
    private boolean studyUpdatedByWeb;
}
