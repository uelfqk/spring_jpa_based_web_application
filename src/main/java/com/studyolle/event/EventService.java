package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Enrollment;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import com.studyolle.enums.EventType;
import com.studyolle.event.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {
    private final int ENROLLMENT_SIZE_ONE = 1;

    private final EventRepository eventRepository;

    public Event createNewEvent(Account account, Study study, EventForm eventForm) {
        Event event = Event.createByEvent(account, study, eventForm.getTitle(), eventForm.getDescription(),
                eventForm.getEndEnrollmentDateTime(), eventForm.getStartDateTime(), eventForm.getEndDateTime(),
                eventForm.getLimitOfEnrollments(), eventForm.getEventType());

        return eventRepository.save(event);
    }

    public void editEvent(Event event, EventForm form) {
        event.editEvent(form.getTitle(),
                form.getDescription(),
                form.getEventType(),
                form.getLimitOfEnrollments(),
                form.getEndEnrollmentDateTime(),
                form.getStartDateTime(),
                form.getEndDateTime());
    }

    public Event enrollEvent(Long eventId, Account account) {
        Event event = eventRepository.findWithStudyWithEnrollmentsById(eventId);
        Enrollment enrollment = Enrollment.createBy(event, account);
        event.addEnrollment(enrollment);
        return event;
    }

    public Event disEnrollEvent(Long eventId, Account account) {
        Event event = eventRepository.findWithStudyWithEnrollmentsById(eventId);
        event.disEnrollEvent(account);
        return event;
    }

    public Event acceptedEnrollAccount(Long eventId, Long enrollId) {
        Event event = eventRepository.findWithEnrollmentsById(eventId, enrollId);

        if(isNotValidEnrollmentSizeOne(event.getEnrollments().size())) {
            throw new IllegalStateException("");
        }

        event.getEnrollments().get(0)
                .acceptedEnrollAccount();

        return event;
    }

    public Event rejectedEnrollAccount(Long eventId, Long enrollId) {
        Event event = eventRepository.findWithEnrollmentsById(eventId, enrollId);

        if(isNotValidEnrollmentSizeOne(event.getEnrollments().size())) {
            throw new IllegalStateException("");
        }

        event.getEnrollments().get(0)
                .rejectedEnrollAccount();

        return event;
    }

    private boolean isNotValidEnrollmentSizeOne(int size) {
        return size != ENROLLMENT_SIZE_ONE;
    }
}
