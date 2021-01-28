package com.studyolle.study;

import com.studyolle.domain.Study;
import com.studyolle.domain.StudyManager;
import com.studyolle.domain.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);

    Study findByPath(String path);


    @Query("select s from Study s " +
            "left outer join fetch s.studyMembers sm " +
            "left outer join fetch sm.member m " +
            "where path = :path")
    Study findStudyAndMembersByPath(@Param("path") String path);

    @Query("select sm from StudyMember sm " +
            "join fetch sm.study s " +
            "join fetch sm.member " +
            "where s.id = :studyId")
    List<StudyMember> findMembers(@Param("studyId") Long studyId);

    @Query("select sm from StudyManager sm " +
            "join fetch sm.study s " +
            "join fetch sm.manager " +
            "where s.id = :studyId")
    List<StudyManager> findManagers(@Param("studyId") Long studyId);
}
