package com.studyolle.event.validator;

import com.studyolle.enums.EventType;
import com.studyolle.event.form.EventForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EventFormValidatorTest {

    private final String PRIVATE_METHOD_IS_NOT_VALID_END_DATE_TIME = "isNotValidEndDateTime";
    private final String PRIVATE_METHOD_IS_NOT_VALID_START_DATE_TIME = "isNotValidStartDateTime";
    private final String PRIVATE_METHOD_IS_NOT_VALID_END_ENROLLMENT_DATE_TIME = "isNotValidEndEnrollmentDateTime";

    EventFormValidator eventFormValidator = new EventFormValidator();

    @Test
    void supportsTest() throws Exception {
        boolean supports = eventFormValidator.supports(EventForm.class);

        assertThat(supports).isTrue();
    }

    @Test
    void isNotValidEndDateTimeFailWithIsBeforeStartDateTimeTest() throws Exception {
        EventForm eventForm = createByEventForm(
                createEndEnrollmentDateTimeByDay(4),
                createStartDateTimeByDay(7),
                createEndDateTimeByDay(6)
        );

        Method method = initializedPrivateMethod(PRIVATE_METHOD_IS_NOT_VALID_END_DATE_TIME);

        assertThat(getResultWithPrivateMethod(method, eventForm)).isTrue();
    }

    @Test
    void isNotValidEndDateTimeFailWithIsBeforeEndEnrollmentDateTimeTest() throws Exception {
        EventForm eventForm = createByEventForm(
                createEndEnrollmentDateTimeByDay(9),
                createStartDateTimeByDay(7),
                createEndDateTimeByDay(8)
        );

        Method method = initializedPrivateMethod(PRIVATE_METHOD_IS_NOT_VALID_END_DATE_TIME);

        assertThat(getResultWithPrivateMethod(method, eventForm)).isTrue();
    }

    @Test
    void isNotValidEndDateTimeSuccessTest() throws Exception {
        EventForm eventForm = createByEventForm(
                createEndEnrollmentDateTimeByDay(4),
                createStartDateTimeByDay(5),
                createEndDateTimeByDay(6)
        );

        Method method = initializedPrivateMethod(PRIVATE_METHOD_IS_NOT_VALID_END_DATE_TIME);

        assertThat(getResultWithPrivateMethod(method, eventForm)).isFalse();
    }

    @Test
    void isNotValidStartDateTimeFailWithIsBeforeEndEnrollmentDatetimeTest() throws Exception {
        EventForm eventForm = createByEventForm(
                createEndEnrollmentDateTimeByDay(4),
                createStartDateTimeByDay(3),
                createEndDateTimeByDay(6)
        );

        Method method = initializedPrivateMethod(PRIVATE_METHOD_IS_NOT_VALID_START_DATE_TIME);

        assertThat(getResultWithPrivateMethod(method, eventForm)).isTrue();
    }

    @Test
    void isNotValidStartDateTimeSuccessTest() throws Exception {
        EventForm eventForm = createByEventForm(
                createEndEnrollmentDateTimeByDay(4),
                createStartDateTimeByDay(5),
                createEndDateTimeByDay(6)
        );

        Method method = initializedPrivateMethod(PRIVATE_METHOD_IS_NOT_VALID_START_DATE_TIME);

        assertThat(getResultWithPrivateMethod(method, eventForm)).isFalse();
    }

    @Test
    void privateIsNotValidEndEnrollmentDateTimeFailWithIsBeforeLocalDateTimeNowDatetimeTest() throws Exception {
        EventForm eventForm = createByEventForm(
                createEndEnrollmentDateTimeByDay(4),
                createStartDateTimeByDay(5),
                createEndDateTimeByDay(6)
        );

        Method method =
                initializedPrivateMethod(PRIVATE_METHOD_IS_NOT_VALID_END_ENROLLMENT_DATE_TIME);

        assertThat(getResultWithPrivateMethod(method, eventForm)).isFalse();
    }

    @Test
    void isNotValidEndEnrollmentDateTimeFailWithIsBeforeLocalDateTimeNowDatetimeTest() throws Exception {
        EventForm eventForm = createByEventForm(
                createEndEnrollmentDateTimeByDay(4),
                createStartDateTimeByDay(5),
                createEndDateTimeByDay(6)
        );

        Errors errors = createByErrors(eventForm);

        eventFormValidator.validate(eventForm, errors);

        FieldError error = errors.getFieldError();

        assertThat(error.getObjectName()).isEqualTo("eventForm");
        assertThat(error.getField()).isEqualTo("endEnrollmentDateTime");
        assertThat(error.getCode()).isEqualTo("wrong.datetime");
        assertThat(error.getDefaultMessage()).isEqualTo("등록 마감 일시를 정확히 입력해주세요.");
        assertThat(errors.hasErrors()).isTrue();
    }

    private Method initializedPrivateMethod(String privateMethodName) throws NoSuchMethodException {
        Method method = getPrivateMethod(privateMethodName);
        method.setAccessible(true);
        return method;
    }

    private boolean getResultWithPrivateMethod(Method method, EventForm eventForm) throws InvocationTargetException, IllegalAccessException {
        return (boolean)method.invoke(eventFormValidator, eventForm);
    }

    private Method getPrivateMethod(String privateMethodName) throws NoSuchMethodException {
        return eventFormValidator.getClass()
                .getDeclaredMethod(privateMethodName, EventForm.class);
    }

    private LocalDateTime createEndEnrollmentDateTimeByDay(int day) {
        return LocalDateTime.of(2021, 03, day, 01, 02, 03);
    }

    private LocalDateTime createStartDateTimeByDay(int day) {
        return LocalDateTime.of(2021, 03, day, 01, 02, 03);
    }

    private LocalDateTime createEndDateTimeByDay(int day) {
        return LocalDateTime.of(2021, 03, day, 01, 02, 03);
    }

    private EventForm createByEventForm(LocalDateTime endEnrollmentDateTime, LocalDateTime startDateTime,
                                LocalDateTime endDateTime) {
        EventForm eventForm = new EventForm();

        eventForm.setEndEnrollmentDateTime(endEnrollmentDateTime);
        eventForm.setStartDateTime(startDateTime);
        eventForm.setEndDateTime(endDateTime);
        return eventForm;
    }

    private Errors createByErrors(EventForm eventForm) {
        return new BeanPropertyBindingResult(eventForm, "eventForm");
    }
}