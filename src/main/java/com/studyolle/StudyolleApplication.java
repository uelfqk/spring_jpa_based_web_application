package com.studyolle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//TODO
// 인텔리제이 커뮤니티 버전 프로파일 설정 -Dspring.profiles.active=dev

@SpringBootApplication
public class StudyolleApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyolleApplication.class, args);
	}

}
