package com.studyolle.domain;

import com.studyolle.account.UserAccount;
import com.studyolle.enums.EventType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
    private List<Enrollment> enrollments;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public boolean isNewEvent() {
        return this.id == null;
    }

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
    }

    public int numberOfRemainSpots() {
        return limitOfEnrollments - enrollments.size();
    }

    public boolean isEnrollableFor(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return getEnrollmentAccountCount(account.getId()) == 0;
    }

    public boolean isDisenrollableFor(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return getEnrollmentAccountCount(account.getId()) > 0;
    }

    public boolean isAttended(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return enrollments.stream().filter(e -> e.getAccount().getId() == account.getId() && e.isAttended())
                .count() > 0;
    }

    private Long getEnrollmentAccountCount(Long accountId) {
        return enrollments.stream().filter(e -> e.getAccount().getId() == accountId)
                .count();
    }
}
