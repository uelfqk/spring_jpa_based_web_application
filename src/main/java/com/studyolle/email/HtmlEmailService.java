package com.studyolle.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Profile("dev")
@Slf4j
@RequiredArgsConstructor
@Component
public class HtmlEmailService implements EmailService {
    private final JavaMailSender javaMailSender;

    //TODO 2021.01.25 47. MimeMessage 전송하기, EmailService 추상화
    //                 1. 대부분 이메일 전송하는 것들을 보면 HTML 로 이루어짐
    //                 2. javaMailSender 의 MimeMessage 를 사용하면 보다 편리하게 작성가능
    //                 3. MimeMessageHelper 는 MimeMessage 를 감싸는 기능
    //                  1). MimeMessageHelper 생성자의 첫번째 인자 : 작성한 mimeMessage,
    //                  2).                          두번째 인자 : multipart (이 이메일에 첨부파일을 보낸다면 true)
    //                  3).                          세번재 인자 : characterSet
    //                 4. 추상화 객체인 EmailService 를 상속 받아 sendEmail 을 구현
    //                 5. EmailMessage 에 전송 데이터를 담아 전송
    @Override
    public void sendEmail(EmailMessage emailMessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo());
            mimeMessageHelper.setSubject(emailMessage.getSubject());

            //TODO 2021.01.25 47. MimeMessage 전송하기, EmailService 추상화
            //                 1. setText 의 boolean html 이 true 이여야 html 형식으로 이메일 전송
            mimeMessageHelper.setText(emailMessage.getMessage(), false);
            javaMailSender.send(mimeMessage);
            log.info("send mail : {}" ,emailMessage.getMessage());
        } catch (MessagingException e) {
            log.error("failed to send email {}", e);
            e.printStackTrace();
        }
    }
}
