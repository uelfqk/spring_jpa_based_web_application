package com.studyolle.event;

import com.studyolle.domain.Account;
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

    public Event createNewEvent(Account account, Study study, EventForm eventForm) {
        Event event = Event.createByEvent(account, study, eventForm.getTitle(), eventForm.getDescription(),
                eventForm.getEndEnrollmentDateTime(), eventForm.getStartDateTime(), eventForm.getEndDateTime(),
                eventForm.getLimitOfEnrollments(), eventForm.getEventType());

        return eventRepository.save(event);
    }
}
