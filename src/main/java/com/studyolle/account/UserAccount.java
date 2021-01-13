package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Arrays;

//TODO 2021.01.13 17.현재 인증된 사용자 정보 참조
//     1. 스프링 시큐리티가 다루는 유저 정보와 우리가 다루는 유저 도메인이 다르므로 이를 연동하는 어뎁터 객체

@Getter
public class UserAccount extends User {
    private Account account;

    public UserAccount(Account account) {
        //TODO 2021.01.13 17.현재 인증된 사용자 정보 참조
        //     1. 유저 도메인 객체의 정보를 스프링 시큐리티가 관리하는 유저 정보에 삽입
        super(account.getNickname(), account.getPassword(), Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        //TODO 2021.01.13 17.현재 인증된 사용자 정보 참조
        //     1. @CurrentUser 애노테이션을 사용하는 핸들러에서 인증된 사용자의 정보를 가져오기 위해 현재 입력된 값으로
        //        해당 프로퍼티에 삽입
        this.account = account;
    }
}
