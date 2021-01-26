package com.studyolle.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyMember {
    @Id @GeneratedValue
    @Column(name = "study_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    public void setStudy(Study study) {
        this.study = study;
        study.getStudyMembers().add(this);
    }

    public static StudyMember createStudyMember(Account member, Study study) {
        StudyMember studyMember = new StudyMember();
        studyMember.setMember(member);
        studyMember.setStudy(study);
        return studyMember;
    }
}
