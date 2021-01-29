package com.studyolle.study;

import com.studyolle.domain.Account;
import com.studyolle.domain.StudyManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyManagerRepository extends JpaRepository<StudyManager, Long> {
//    @Query("select sm from StudyManager sm " +
////            "join fetch sm.manager " +
//            "where sm.study_id = :studyId")
//    StudyManager findAccountFetchJoin(@Param("studyId") Long studyId);

    @Query("select sm from StudyManager sm " +
            "join fetch sm.manager m " +
            "join fetch sm.study s " +
            "where s.id = :studyId")
//            "join fetch sm.manager m " +
//            "where s.study_id = :studyId")
    List<StudyManager> findAccountFetchJoinByStudyId(@Param("studyId") Long studyId);

}
