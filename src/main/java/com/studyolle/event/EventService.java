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
            Enrollment enrollment = Enrollment.createBy(account);
            event.addEnrollment(enrollment);
//            enrollmentRepository.save(enrollment);
        }
    }

    public Event disEnrollmentEvent(Long eventId, Account account) {
        Event event = eventRepository.findWithStudyWithEnrollmentsById(eventId);
        event.disEnrollEvent(account);
        return event;
    }

    public void disEnrollmentEvent(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        event.disEnrollEvent(enrollment);
    }

    public void acceptedEnrollAccount(Event event, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findByEventAndId(event, enrollmentId);
        enrollment.acceptedEnrollAccount();
    }

    public void rejectedEnrollAccount(Event event, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findByEventAndId(event, enrollmentId);
        enrollment.rejectedEnrollAccount();
    }

    public void checkInEvent(Event event, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findByEventAndId(event, enrollmentId);
        enrollment.checkIn();
    }

    public void cancelCheckInEvent(Event event, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findByEventAndId(event, enrollmentId);
        enrollment.cancelCheckIn();
    }
}
