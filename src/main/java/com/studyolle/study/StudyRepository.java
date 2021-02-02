package com.studyolle.study;

import com.studyolle.domain.Study;
import com.studyolle.domain.StudyManager;
import com.studyolle.domain.StudyMember;
import com.studyolle.domain.StudyTag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);

//    @EntityGraph(value = "studyWithAll", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @Query("select sm from StudyMember sm " +
            "join fetch sm.study s " +
            "join fetch sm.member " +
            "where s.id = :studyId")
    List<StudyMember> findStudyMembersByStudyId(@Param("studyId") Long studyId);

    @Query("select s from Study s " +
            "left outer join fetch s.studyManagers sm " +
            "left outer join fetch sm.manager m " +
            "where path = :path")
    Study findStudyAndManagersByPath(@Param("path") String path);

    @Query("select s from Study s " +
            "left outer join fetch s.studyMembers sm " +
            "left outer join fetch sm.member m " +
            "where path = :path")
    Study findStudyAndMembersByPath(@Param("path") String path);

    @Query("select sm from StudyManager sm " +
            "join fetch sm.manager " +
            "join fetch sm.study s " +
            "where s.id = :studyId")
    List<StudyManager> findStudyManagersByStudyId(@Param("studyId") Long studyId);

    @Query("select s from Study s " +
            "join fetch s.studyAccounts sa " +
            "join fetch sa.account " +
            "where s.path = :path")
    Study findStudyAccountsByPath(@Param("path") String path);

    @Query("select s from Study s " +
            "left outer join fetch s.studyTags st " +
            "left outer join fetch st.tag " +
            "where s.path = :path")
    Study findStudyTagsByPath(@Param("path") String path);

    @Query("select s from Study s " +
            "left outer join fetch s.studyZones sz " +
            "left outer join fetch sz.zone " +
            "where path = :path")
    Study findStudyZonesByPath(@Param("path") String path);
}
