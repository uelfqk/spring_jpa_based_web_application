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
}
