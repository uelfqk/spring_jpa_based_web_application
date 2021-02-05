package com.studyolle.domain;

import com.studyolle.account.UserAccount;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//TODO 2021.01.26 50. 스터디 도메인 개발

@Entity
@Getter @Setter
//@NamedEntityGraph(name = "studyWithAll", attributeNodes = {
//        @NamedAttributeNode("studyTags"),
//        @NamedAttributeNode("studyZones"),
//        @NamedAttributeNode("studyAccounts")
//})
public class Study {
    @Id @GeneratedValue
    @Column(name = "study_id")
    private Long id;

    //TODO 스터디 관리자
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyManager> studyManagers = new ArrayList<>();

    //TODO 스터디 참여 계정
    @OneToMany(mappedBy = "study")
    private List<StudyMember> studyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<StudyAccount> studyAccounts = new ArrayList<>();

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
    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<StudyTag> studyTags = new ArrayList<>();

    //TODO 스터디에 등록된 지역 정보
    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<StudyZone> studyZones = new ArrayList<>();

    //TODO 스터디 게시 일자
    private LocalDateTime publishedDateTime;

    //TODO 스터디 종료 일자
    private LocalDateTime closedDateTime;

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
        this.studyManagers.add(studyManager);
        studyManager.setStudy(this);
    }

    public void addStudyMember(StudyMember studyMember) {
        this.studyMembers.add(studyMember);
        studyMember.setStudy(this);
    }

    public void addStudyAccount(StudyAccount studyAccount) {
        this.studyAccounts.add(studyAccount);
        studyAccount.setStudy(this);
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
        for (StudyAccount studyAccount : studyAccounts) {
            if(studyAccount.isManager(userAccount.getAccount())) {
                return true;
            }
        }
        return false;
    }

    public String getEncodingPath() throws UnsupportedEncodingException {
        return URLEncoder.encode(this.path, String.valueOf(StandardCharsets.UTF_8));
    }

    public void addStudyTag(StudyTag studyTag) {
        this.studyTags.add(studyTag);
        studyTag.setStudy(this);
    }

    public void removeStudyTag(Tag tag) {
        studyTags.removeIf(at -> at.getTag().equals(tag));
    }

    public void addStudyZone(StudyZone studyZone) {
        this.studyZones.add(studyZone);
        studyZone.setStudy(this);
    }

    public void removeStudyZone(Zone zone) {
        studyZones.removeIf(z -> z.getZone().equals(zone));
    }

    public boolean isRemovable() {
        return isPublished() && isRecruiting();
    }

    public boolean canRecruiting() {
        return this.published && this.recruitingUpdateDatetime == null ||
                recruitingUpdateDatetime.isBefore(LocalDateTime.now().minusHours(1));
    }

    public void publish() {
        if(!this.closed && !this.published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
            return;
        }

        throw new RuntimeException("스터디를 공개 할 수 없는 상태입니다. 스터디를 이미 공개했거나 종료했습니다.");
    }

    public void close() {
        if(!this.closed && this.published) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
            return;
        }

        throw new RuntimeException("스터디를 종료 할 수 없는 상태입니다. 스터디를 공개하지 않았거나 이미 종료한 스터디입니다.");
    }

    public void startRecruit() {
        if(canRecruiting()) {
            this.recruiting = true;
            this.recruitingUpdateDatetime = LocalDateTime.now();
            return;
        }

        throw new RuntimeException("인원 모집을 시작할 수 없습니다. 스터디를 공개하거나 한 시간 뒤에 다시 시도하세요.");
    }

    public void stopRecruit() {
        if(canRecruiting() && recruiting) {
            this.recruiting = false;
            this.recruitingUpdateDatetime = LocalDateTime.now();
            return;
        }
    }
}