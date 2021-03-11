package com.studyolle.domain;

import com.studyolle.enums.EventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    @Column(name = "enrollment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    //TODO 62. 모임 도메인
    //      1. 선착순 5명 설정인데 10명의 유저가 참가신청을 할때 
    //         1 번째 참가 신청한 사람이 취소하게되면 자동으로 6번째 대기자가 참가 확정이 되어야한다.
    //      2. 이때 참가 신청한 시간을 가져와서 정렬하고 6번째 대기자를 찾아 참가 확정처리하기 때문에
    //         매우 중요한 값이 된다.
    private LocalDateTime enrolledAt;

    //TODO 62. 모임 도메인
    //      1. 참가 상태
    private boolean accepted;

    //TODO 62. 모임 도메인  
    //      1. 실제로 모임에 참가를 했는지 안했는지 판단
    private boolean attended;

    public static Enrollment createBy(Event event, Account account) {
        Enrollment enrollment = new Enrollment();
        enrollment.setEvent(event);
        enrollment.setAccount(account);
        enrollment.setEnrolledAt(LocalDateTime.now());

        if(event.isFCFSEnrollment()) {
            enrollment.setAccepted(true);
        }

        return enrollment;
    }

    public void acceptedEnrollAccount() {
        this.accepted = true;
    }

    public void rejectedEnrollAccount() {
        this.accepted = false;
    }
}
