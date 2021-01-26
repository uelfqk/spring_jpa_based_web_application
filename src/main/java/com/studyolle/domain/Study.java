package com.studyolle.domain;

import lombok.Getter;
import lombok.Setter;

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
}