package com.studyolle.event.form;

import com.studyolle.enums.EventType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

//TODO 2021.02.20 62. 모임 만들기
//                 1. 모임 만들기 뷰 랜더링을 위해 필요한 폼 데이터 클래스
//                 2. 모임 모집 전략 -> 기본 선착순
//                 3. 시간에 관련된 데이터를 폼으로 부터 받을 때의 전략 -> ISO DATE_TIME
//                 4. 모임 최소 모집 인원 -> 2
//                    기본값을 2로 설정

@Getter @Setter
@ToString
public class EventForm {

    @NotBlank
    @Length(max = 50)
    private String title;

    private String description;
    
    private EventType eventType = EventType.FCFS;

    @Min(2)
    private Integer limitOfEnrollments = 2;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endEnrollmentDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;
}
