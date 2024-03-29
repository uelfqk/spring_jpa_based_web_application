package com.studyolle.domain;

import com.studyolle.account.UserAccount;
import com.studyolle.enums.EventType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

//TODO 62. 모임 도메인

@Entity
@Getter @Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Event {

    @Id @GeneratedValue
    @Column(name = "event_id")
    private Long id;

    //TODO 62. 모임 도메인
    //      1. 스터디 내부에서 모임을 만드는 형태
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    //TODO 62. 모임 도메인
    //      1. 모임을 만든 유저의 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    //TODO 62. 모임 도메인
    //      1. 프리미티브 타입을 래핑함으로서 null 값을 허용
    //      2. 모임 인원 제한 수
    private Integer limitOfEnrollments;

    @OneToMany(mappedBy = "event", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public static Event createByEvent(Account account, Study study, String title, String description,
                                      LocalDateTime endEnrollmentDateTime, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                      Integer limitOfEnrollments, EventType eventType) {
        Event event = new Event();
        event.setCreatedBy(account);
        event.setStudy(study);
        event.setTitle(title);
        event.setDescription(description);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(endEnrollmentDateTime);
        event.setStartDateTime(startDateTime);
        event.setEndDateTime(endDateTime);
        event.setLimitOfEnrollments(limitOfEnrollments);
        event.setEventType(eventType);
        return event;
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setEvent(this);
    }

    public static Event defaultEvent() {
        return new Event();
    }

    public void editEvent(String title, String description, EventType eventType ,Integer limitOfEnrollments,
                          LocalDateTime endEnrollmentDateTime, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.title = title;
        this.description = description;
        this.eventType = eventType;
        this.limitOfEnrollments = limitOfEnrollments;
        this.endEnrollmentDateTime = endEnrollmentDateTime;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;

        if(isFCFSEnrollment()) {
            nextAccountEnrollEvent();
        }
    }

    public int numberOfRemainSpots() {
        return limitOfEnrollments - (int)enrollments.stream().
                filter(er -> er.isAccepted()).count();
    }

    public boolean isEnrollableFor(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return getEnrollmentAccountCount(account) == 0;
    }

    public boolean isDisEnrollableFor(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return getEnrollmentAccountCount(account) > 0;
    }

    public boolean isAttended(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return enrollments.stream()
                .filter(e -> e.getAccount().equals(account) && e.isAttended())
                .count() > 0;
    }

    public boolean isLimitOverEnroll() {
        return enrollments.size() >= limitOfEnrollments;
    }

    public boolean canAccept(Enrollment enrollment) {
        // TODO 2021.03.15 모임 참가 신청
        //                 모임 모집 방식이 관리자 확인
        //                 && 모임 신청 취소한 객체가 현재 모임 컬랙션에 포함
        //                 && ! 모임에 체크인
        //                 && ! 모임 신청 객체가 참가 수락
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && !enrollment.isAccepted();
    }

    public boolean canReject(Enrollment enrollment) {
        // TODO 2021.03.15 모임 참가 취소
        //                 모임 모집 방식이 관리자 확인
        //                 && 모임 신청 취소한 객체가 현재 모임 컬랙션에 포함
        //                 && ! 모임에 체크인
        //                 && 모임 신청 객체가 참가 수락
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && enrollment.isAccepted();
    }

    private boolean isAbleToAcceptWaitingEnrollment() {
        return this.eventType == EventType.FCFS
                && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments();
    }

    public long getNumberOfAcceptedEnrollments() {
        return this.enrollments.stream().filter(en -> en.isAccepted()).count();
    }

    private Long getEnrollmentAccountCount(Account account) {
        return enrollments.stream().filter(e -> e.getAccount().equals(account))
                .count();
    }

    public void disEnrollEvent(Account account) {
        Enrollment enrollment = enrollments.stream()
                .filter(r -> r.getAccount().equals(account))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("회원정보가 잘못되었습니다."));

        enrollments.remove(enrollment);

        if(isFCFSEnrollment()) {
            nextAccountEnrollEvent();
        }
    }

    public void disEnrollEvent(Enrollment enrollment) {
        enrollments.remove(enrollment);
        acceptNextAccountEnrollEvent();
    }

    private Enrollment getFirstWaitingEnrollment() {
        for(Enrollment enrollment : enrollments) {
            return getFirstWaitingEnrollment(enrollment);
        }

        return null;
    }

    private Enrollment getFirstWaitingEnrollment(Enrollment enrollment) {
        if(enrollment.isAccepted()) {
            return enrollment;
        }

        return null;
    }

    private void acceptNextAccountEnrollEvent() {
        if(this.isAbleToAcceptWaitingEnrollment()) {
            acceptNextAccountEnrollEvent(this.getFirstWaitingEnrollment());
        }
    }

    private void acceptNextAccountEnrollEvent(Enrollment enrollment) {
        if(enrollment != null) {
            enrollment.setAccepted(true);
        }
    }

    private void nextAccountEnrollEvent() {
        for(Enrollment enrollment : enrollments) {
            if (!isEnroll()) {
                break;
            }

            enrollment.setAccepted(true);
        }

//        Enrollment enrollment = enrollments.stream().filter(e -> !e.isAccepted())
//                .findFirst().orElse(null);
//
//        if(enrollment == null) {
//            return;
//        }
//
//        enrollment.setAccepted(true);
    }

    public boolean isFCFSEnrollment() {
        return eventType == EventType.FCFS && isEnroll();
    }

    private boolean isEnroll() {
        return numberOfRemainSpots() != 0;
    }
}
