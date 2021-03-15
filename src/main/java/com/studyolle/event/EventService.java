package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Enrollment;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import com.studyolle.event.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {
    private final int ENROLLMENT_SIZE_ONE = 1;

    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;

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

    public void enrollmentEvent(Event event, Account account) {
        if(!enrollmentRepository.existsByAccountAndEvent(account, event)) {
            Enrollment enrollment = Enrollment.createBy(event, account);
            event.addEnrollment(enrollment);
        }
    }

    public Event disEnrollmentEvent(Long eventId, Account account) {


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

        getEnrollmentOne(event).rejectedEnrollAccount();

        return event;
    }

    private boolean isNotValidEnrollmentSizeOne(int size) {
        return size != ENROLLMENT_SIZE_ONE;
    }

    private Enrollment getEnrollmentOne(Event event) {
        if(isNotValidEnrollmentSizeOne(event.getEnrollments().size())) {
            throw new IllegalStateException("");
        }

        return event.getEnrollments().get(0);
    }

    public Event checkInEvent(Long eventId, Long enrollId) {
        Event event = eventRepository.findWithEnrollmentsById(eventId, enrollId);

        getEnrollmentOne(event).checkIn();

        return event;
    }

    public Event cancelCheckInEvent(Long eventId, Long enrollId) {
        Event event = eventRepository.findWithEnrollmentsById(eventId, enrollId);

        getEnrollmentOne(event).cancelCheckIn();

        return event;
    }
}
