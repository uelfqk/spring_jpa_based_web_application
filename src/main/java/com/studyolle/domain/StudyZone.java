package com.studyolle.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyZone {
    @Id @GeneratedValue
    @Column(name = "study_zone_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;

    private void setStudy(Study study) {
        this.study = study;
        study.getStudyZones().add(this);
    }

    public static StudyZone createStudyZone(Study study, Zone zone) {
        StudyZone studyZone = new StudyZone();
        studyZone.setStudy(study);
        studyZone.setZone(zone);
        return studyZone;
    }
}
