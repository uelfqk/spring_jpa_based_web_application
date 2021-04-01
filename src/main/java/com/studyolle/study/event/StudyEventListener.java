package com.studyolle.study.event;

import com.studyolle.domain.Study;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Async
@Component
@Transactional(readOnly = true)
public class StudyEventListener {

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyCreatedEvent.getStudy();
        log.info(study.getTitle() + " is created");
        // TODO 이메일 보내거나, DB에 Notification 정보를 저장하면 된다.

        // TODO 아래와 같이 이벤트 발행 중 예외가 발생하면 현재 실행된 트랜잭션이 실패하면서
        //      롤백하게된다. -> 스터디 개설이 롤백 (같은 스레드에서 처리되기 때문)
        //      따라서 스터디 개설 로직에 영향을 주지 않으면서 이벤트를 발행하는 처리가 필요하다.
        throw new RuntimeException();

    }
}
