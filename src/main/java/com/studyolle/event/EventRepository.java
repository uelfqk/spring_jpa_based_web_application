package com.studyolle.event;

import com.studyolle.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event e " +
            "join fetch e.createdBy " +
            "left join fetch e.enrollments em " +
            "left join fetch em.account " +
            "where e.id = :eventId")
    Event findWithCreateByWithEnrollmentsById(@Param("eventId") Long eventId);

    @Query("select e from Event e " +
            "join fetch e.study " +
            "left join fetch e.enrollments em " +
            "left join fetch em.account " +
            "where e.id = :eventId " +
            "order by em.enrolledAt asc")
    Event findWithStudyWithEnrollmentsById(@Param("eventId") Long eventId);

    @Query("select distinct e from Event e " +
            "left join fetch e.enrollments " +
            "join fetch e.study s " +
            "where s.id = :studyId")
    List<Event> findAllByStudyId(@Param("studyId") Long StudyId);

    @Query("select e from Event e " +
            "left join fetch e.enrollments em " +
            "where e.id = :eventId and em.id = :enrollId")
    Event findWithEnrollmentsById(@Param("eventId") Long eventId, @Param("enrollId") Long enrollId);

    @Query("select e from Event e " +
            "left join fetch e.enrollments em " +
            "where e.id = :eventId")
    Event findWithEnrollmentsById(@Param("eventId") Long eventId);
}
