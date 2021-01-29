package com.studyolle.domain;

import com.studyolle.account.UserAccount;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//TODO 2021.01.26 50. 스터디 도메인 개발

@Entity
@Getter @Setter
public class Study {
    @Id @GeneratedValue
    @Column(name = "study_id")
    private Long id;

    //TODO 스터디 관리자
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "manager", cascade = CascadeType.PERSIST)
    private List<StudyManager> studyManagers = new ArrayList<>();

    //TODO 스터디 참여 계정
    @OneToMany(mappedBy = "member")
    private List<StudyMember> studyMembers = new ArrayList<>();

    //TODO 스터디 url path
    @Column(unique = true)
    private String path;

    //TODO 스터디 제목
    private String title;
    
    //TODO 스터디 짧은 소개
    private String shortDescription;

    //TODO 스터디 긴 소개
    @Lob @Basic(fetch = FetchType.LAZY)
    private String fullDescription;

    //TODO 스터디 게시글에 사용될 이미지
    @Lob @Basic(fetch = FetchType.LAZY)
    private String image;

    //TODO 스터디에 등록된 태그 정보
    @OneToMany
    private List<StudyTag> studyTags = new ArrayList<>();

    //TODO 스터디에 등록된 지역 정보
    @OneToMany
    private List<StudyZone> studyZones = new ArrayList<>();

    //TODO 스터디 게시 일자
    private LocalDateTime publishedDateTime;

    //TODO 스터디 종료 일자
    private LocalDateTime closeDateTime;

    //TODO 스터디 여닫는 시간 제한
    private LocalDateTime recruitingUpdateDatetime;

    //TODO 스터디 참여 여부
    private boolean recruiting;

    //TODO 스터디 게시 여부
    private boolean published;

    //TODO 스터디 종료 여부
    private boolean closed;

    //TODO 스터디 베너 사용 여부
    private boolean useBanner;

    public void addStudyManager(StudyManager studyManager) {
        this.getStudyManagers().add(studyManager);
        studyManager.setStudy(this);
    }

    //TODO 2021.01.27 52. 스터디 조회
    //                 1. 타임 리프가 제공하는 베리어블 익스프레션에서 호출하는 메소드
    //                     타임 리프 베리어블 익스프레션 = 스프링 익스프레션임으로 메소드 호출 가능
    //                 2. 가입 가능한 스터디인지 확인
    //                  1). 스터디가 공개되었고
    //                  2). 스터디 모집중이며
    //                  3). 해당 회원이 해당 스터디에 가입되지 않은 상태이고
    //                  4). 해당 회원이 해당 스터디의 관리자가 아닐때
    //                 3. 그 결과로 스터디 가입버튼 보여주기
    //                  1). th:if="${study.isJoinable(#authentication.principal)}"
    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.studyMembers.contains(account) && !this.studyManagers.contains(account);
    }

    //TODO 2021.01.27 52. 스터디 조회
    //                 1. 해당 스터디에 해당 회원이 가입되어있는지 확인
    //                 2. 그 결과로 스터디 탈퇴 버튼 보여주기
    //                  1). th:if="${!study.closed && study.isMember(#authentication.principal)}"
    //                  2). 타임리프 탬플릿의 추가 조건 -> 해당스터디가 종료 상태가 아니고
    public boolean isMember(UserAccount userAccount) {
        return this.studyMembers.contains(userAccount.getAccount());
    }

    //TODO 2021.01.27 52. 스터디 조회
    //                 1. 해당 스터디에 해당 회원이 매니저인지 확인
    //                 2. 그 결과로 모임만들기 버튼 보여주기
    //                  1). th:if="${study.published && !study.closed && study.isManager(#authentication.principal)}
    //                  2). 타임리프 탬플릿의 추가 조건
    //                   -. 해당 스터디가 공개 되었고
    //                   -. 해당 스터디가 종료 되지 않았고
    public boolean isManager(UserAccount userAccount) {
        return this.studyManagers.contains(userAccount.getAccount());
    }
}