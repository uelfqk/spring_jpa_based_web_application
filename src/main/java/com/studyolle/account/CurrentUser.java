package com.studyolle.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//TODO 2021.01.13 17.현재 인증된 사용자 정보 참조
//     1. 컨트롤러(핸들러) 에서 사용하는 유저 객체가 @CurrentUser 애노테이션을 참조하고 있을때
//      1). 해당 유저의 인증 정보가 없다면 anonymousUser (익명 사용자) 문자열이면 account = null
//      2). 인증 정보를 가지고 있는 유저라면 account 객체를 반환
//     2. 이 반환하는 account 객체는 UserAccount 객체에서 Getter 로 가져오는 것이다.
//     3. @AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
//     4. @Retention(RetentionPolicy.RUNTIME) : 런타임까지 유지
//     5. @Target(ElementType.PARAMETER) : 프로퍼티에만 사용가능
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentUser {
}
