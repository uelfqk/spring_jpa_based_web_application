package com.studyolle.event.validator;

import com.studyolle.event.form.EventForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class EventFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return EventForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventForm eventForm = (EventForm)target;

        // 등록 마감 일시 검증
        if(isNotValidEndEnrollmentDateTime(eventForm)) {
            errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "등록 마감 일시를 정확히 입력해주세요.");
        }
        // 모임 시작 일시 검증
        if(isNotValidStartDateTime(eventForm)) {
            errors.rejectValue("startDateTime", "wrong.datetime", "모임 시작 일시를 정확히 입력해주세요.");
        }
        // 모임 종료 일시 검증
        if(isNotValidEndDateTime(eventForm)) {
            errors.rejectValue("endDateTime", "wrong.datetime", "모임 종료 일시를 정확히 입력해주세요.");
        }
    }

    //TODO 2021.02.22 63. 모임 만들기 폼 서브밋
    //                 1. 모임 시작 일시 검증
    //                  1). 폼에서 입력받은 모임 시작시간이 모임 등록 마감 일시 이후 인지 검증
    private boolean isNotValidStartDateTime(EventForm eventForm) {
        return eventForm.getStartDateTime().isBefore(eventForm.getEndEnrollmentDateTime());
    }

    //TODO 2021.02.22 63. 모임 만들기 폼 서브밋
    //                 1. 모임 종료 일시 검증
    //                  1). 폼에서 입력받은 모임 종료 일시가 모임 시작 일시 이후 인지와
    //                                                 모임 등록 마감 일시 이후 인지 검증
    private boolean isNotValidEndDateTime(EventForm eventForm) {
        return eventForm.getEndDateTime().isBefore(eventForm.getStartDateTime()) ||
                eventForm.getEndDateTime().isBefore(eventForm.getEndEnrollmentDateTime());
    }

    //TODO 2021.02.22 63. 모임 만들기 폼 서브밋
    //                 1. 모임 마감 일시 검증
    //                  1). 폼에서 입력받은 모임 등록 마감 일시가 현재 시간 이후 인지 검증
    private boolean isNotValidEndEnrollmentDateTime(EventForm eventForm) {
        return eventForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now());
    }
}