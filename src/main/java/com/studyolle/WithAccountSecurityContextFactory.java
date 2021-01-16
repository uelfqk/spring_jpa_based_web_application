package com.studyolle;

import com.studyolle.account.AccountService;
import com.studyolle.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

//TODO 2021.01.16 26.프로필 수정 테스트
//     1. @WithAccount 애노테이션을 이용해서 테스트를 하기 때문에
//        각 테스트 마다 유저 정보를 삭제해주어야 정상적으로 테스트가 가능하다.
@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String nickname = withAccount.value();

        //TODO 2021.01.16 26.프로필 수정 테스트
        //     1. 유저 정보를 저장하는 행위도 여기서 함께 처리
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname(nickname);
        signUpForm.setEmail(nickname + "@email.com");
        signUpForm.setPassword("12345678");

        accountService.processNewAccount(signUpForm);
        //TODO 2021.01.16 26.프로필 수정 테스트
        //     1. @WithUserDetails 을 사용해서 처리하는 로직이 아래의 코드
        //      1). @WithUserDetails(value = "youngbin", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        //     2. 계정을 만든 뒤에 SecurityContext 해당 계정으로 인증 정보를 만들고 넣어준다.
        UserDetails principal = accountService.loadUserByUsername(nickname);
        //TODO 2021.01.16 26.프로필 수정 테스트
        //     1. AccountService 의 login(Account account) 에서와 같이 인증 정보 발행
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        //TODO 2021.01.16 26.프로필 수정 테스트
        //     1. 비어있는 SecurityContext 를 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        //TODO 2021.01.16 26.프로필 수정 테스트
        //     1. SecurityContext 에 인증정보를 전달
        context.setAuthentication(authentication);
        return context;
    }
}
