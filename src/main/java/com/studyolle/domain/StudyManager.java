package com.studyolle.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyManager {
    @Id @GeneratedValue
    @Column(name = "study_manager_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account manager;

    public static StudyManager createStudyManager(Account manager) {
        StudyManager studyManager = new StudyManager();
        studyManager.setManager(manager);
        return studyManager;
    }
}
