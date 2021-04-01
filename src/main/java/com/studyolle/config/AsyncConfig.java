package com.studyolle.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {


    // TODO
    //      1. 현재 일하고 있는 스레드 개수가 코어(core pool size) 보다 작으면
    //         남아있는 스레드를 사용
    //      2. 현재 일하고 있는 스레드 개수가 코어 개수만큼 차있으면 큐 용량(queue capacity)이
    //         찰때가지 큐에 쌓아둔다.
    //      3. 큐 용량이 다 차면, 코어 개수를 넘어서 맥스 개수(max pool size)에 다르기 전까지
    //         새로운 스레드를 만들어 처리한다.
    //      4. 맥스 개수를 넘기면 테스크를 처리하지 못한다.

    // TODO
    //      executor.setCorePoolSize(processor);
    //      1. 수영장에 10 개의 튜브가 있을때 3명의 사람이 튜브를 이용중일때
    //         새로운 사람이 들어오면 1개의 튜브를 준다.
    //
    //      executor.setQueueCapacity(50);
    //      2. 수영장에 10 개의 튜브를 10명의 사람이 사용중일때 새로운 사람이 오면
    //         줄을 세운다. [50 번째가지]
    //
    //      executor.setQueueCapacity(50);
    //      3. 51번째 사람이 오면 새로운 튜브를 생성하여 주게된다.
    //         max pool size 의 범위 안에서
    //
    //      4. 수영장에서 20개의 튜브까지 사용할수 있고 20개가 모두 찼다면
    //         요청을 처리하지 못하게 된다.
    //
    //      executor.setKeepAliveSeconds(60);
    //      5. 서비스에서 최적의 스레드 풀 상태를 유지하기위해 60초 후에
    //         core pool size 보다 더 많은 스레드가 생성됬을때 사용하지 않는 풀을 수거
    //
    //      executor.setThreadNamePrefix("AsyncExecutor~");
    //      6. 이벤트 처리 스레드 이름 부여
    //         서비스중 예외 발생 등의 처리시 해당 스레드를 더 빠르게 찾기위해 사용
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processor = Runtime.getRuntime().availableProcessors();
        log.info("processor count {}", processor);
        // TODO 처리하는 방식에 따라 값이 달라진다.
        executor.setCorePoolSize(processor);
        executor.setMaxPoolSize(processor * 2);
        // TODO 기본값 : 21억개
        //      기본값을 사용하게되면 executor.setMaxPoolSize(processor * 2); 의미가 없어짐
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("AsyncExecutor~");
        executor.initialize();
        return executor;
    }
}
