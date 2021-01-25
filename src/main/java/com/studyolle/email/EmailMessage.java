package com.studyolle.email;

import lombok.Getter;
import lombok.Setter;

//TODO 2021.01.25 47. MimeMessage 전송하기, EmailService 추상화
//                 1. 이메일 전송 데이터를 담을 객체 - 자바 빈
@Getter @Setter
public class EmailMessage {

    private String to;

    private String subject;

    private String message;
}
