package com.studyolle.email;

//TODO 2021.01.25 47. MimeMessage 전송하기, EmailService 추상화
//                 1. EmailService 추상화
//                 2. AccountService 에서 사용하던 javaMailSender 의 구현체인 ConsoleMailSender 를
//                    profile 이 dev 로 변경되면서 더이상 사용하지 않는데
//                    기존 이메일 전송 코드와 새로 추가된 html 이메일 전송 코드를 추상화하여
//                    local 환경 일때는 console 로 dev 환경일때는 html 이메일 전송 코드를 주입 받아서 사용할 수 있도록 수정 
public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}
