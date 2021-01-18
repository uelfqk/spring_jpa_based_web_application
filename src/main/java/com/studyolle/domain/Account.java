package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

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
public class Account {
    @Id @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    //TODO 로그인에 필요한 정보 ( 이메일 )
    @Column(nullable = false, unique = true)
    private String email;

    //TODO 로그인에 필요한 정보 ( 아이디 )
    @Column(nullable = false, unique = true)
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

    //TODO 2021.01.18 35 관심주제 도메인
    //     1. 유저가 Tag 에 관심있는 사람을 조회하는 기능은 없고
    //     2. 어떤 유저가 어떤 Tag 를 가지고 있는지에 대허서 더 관심이 많도록 설정
    //     3. 강의에서는 ManyToMany 관계로 설정하였으나 여기서는 OneToMany - ManyToOne 관계로 설정
    //      1). 중간 테이블을 엔티티로 승격시켜 관리
    @OneToMany(mappedBy = "account")
    private Set<AccountTag> accountTags = new HashSet<>();

    //TODO 2021.01.13 16.가입확인 이메일 재전송
    //     이메일 전송 토큰 생성 시간
    //     이메일 토큰을 생성할때 현재시간을 삽입
    private LocalDateTime emailCheckTokenGeneratedAt;

    public void addAccountTag(AccountTag accountTag) {
        this.accountTags.add(accountTag);
        accountTag.setAccount(this);
    }

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
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

    //TODO 2021.01.10 - 11.회원가입 인증 메일 확인 테스트 및 리팩토링
    //     Controller 가 아닌 엔티티 클래스에서 처리하도록 리팩토링
    //     엔티티 클래스로 옮겨오면서 응집도 향상
    public void completeSignUp() {
        //TODO 브라우저에서 전송한 내용에 이상이 없는 경우
        //     이메일 인증 처리
        //     가입일시를 현재시간으로 변경
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    //TODO 2021.01.10 - 12.회원가입 가입완료 후 자동로그인
    //     리팩토링
    //     AccountController 에서 조건문을 읽기 힘듦으로 도메인으로 가져와
    //     폼으로부터 넘어온 매개변수를 비교하여 boolean 값으로 반환
    //     도메인 객체 응집도 향상
    public boolean isValidEmailToken(String token) {
        return emailCheckToken.equals(token);
    }

    //TODO 2021.01.13 16.가입확인 이메일 재전송
    //     인증 이메일 재전송 주기를 1시간으로 설정하기 위하여 검증하는 로직
    //     현재 시간에서 1시간을 뺀 값이 토큰 생성시간 이후인지 확인
    //     before : true / after : false
    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
