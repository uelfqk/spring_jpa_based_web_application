package com.studyolle.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

//TODO 2021.01.15 21.로그인 기억하기
//     1. JdbcTokenRepository 를 사용하기 위한 엔티티 생성
//      1). JdbcTokenRepository -> CREATE_TABLE_SQL 참고
//     2. rememberMe 쿠키 발급시에 사용되는 정보들을 정의
//     3. JPA 를 사용하기 때문이 이와 같이 정의해주면 스키마로 테이블 생성
//     4. 쿠키를 통해 인증정보가 전달되는 경우 해당 테이블의 저장된 정보를 통해 인증 시도
//     5. 인증정보중 username 과 series 는 동일하지만 token 이 다른 경우
//        해당 쿠키가 탈취 당했다고 판단하여 token 정보를 삭제
@Entity
@Getter @Setter
@Table(name = "persistent_logins")
public class PersistentLogins {

    @Id
    @Column(length = 64, nullable = false)
    private String series;

    @Column(length = 64, nullable = false)
    private String username;

    @Column(length = 64, nullable = false)
    private String token;

    @Column(name = "last_used", nullable = false)
    private LocalDateTime lastUsed;
}
