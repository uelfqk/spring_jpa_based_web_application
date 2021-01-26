package com.studyolle.account;

import com.studyolle.account.form.SignUpForm;
import com.studyolle.config.AppProperties;
import com.studyolle.domain.*;
import com.studyolle.email.EmailMessage;
import com.studyolle.email.EmailService;
import com.studyolle.settings.form.Notifications;
import com.studyolle.settings.form.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.stream.Collectors;

//TODO 2021.01.10 - 이메일 전송에 필요한 토큰 발행 후 데이터베이스에 저장되지 않는 이슈
//     processNewAccount(SignUpForm signUpForm) 메소드를 실행할때는 이미 트랜젝션이 종료된 상태
//     따라서 newAccount.generateEmailCheckToken() 는 준영속 상태의 객체에 데이터를 변경한것이다.
//     해당 메소드에 @Transactional 애노테이션을 추가하여 영속상태를 유지시켜주면 변경 내용이 반영되어
//     데이터베이스에 업데이트가 발생하게 된다.
//     테스트 코드를 작성하지 않아 확인되지 않은 문제

//TODO 2021.01.15 - 23.Open EntityManager (or Session) In View 필터
//     1. 스프링부트는 Open Session In View 가 true 상태
//      1). 즉, 트랜잭션 범위 내라면 뷰를 랜더링 하는 시점에도 데이터를 추가로 읽어올 수 있다.
// ------------------------------------------------------------------------------------------------------------
//     2. 개발 중 발생 버그
// ------------------------------------------------------------------------------------------------------------
//      1). 증상 : 이메일 인증을 하였으나 유저 도메인에서 이메일 인증 필드와, 가입일시가 업데이트 되지 않음
// ------------------------------------------------------------------------------------------------------------
//      2). 원인 : 해당 필드는 Controller Layer 에서 변경하는데 이때 트랜잭션의 범위를 벗어나 객체의 데이터는 변경되었지만
//                영속성 컨텍스트는 트랜잭션이 끝난 순간부터 비워져 변경감지가 발생하지 않음
// ------------------------------------------------------------------------------------------------------------
//      3). 수정 : 해당 도메인 객체의 필드 변경을 Controller Layer 에서 Service Layer 로 이전하고
//                해당 메소드에 @Transactional 애노테이션을 추가하여 메소드 실행시 트랜잭션 상태를 유지하도록 변경
// ------------------------------------------------------------------------------------------------------------
//      4). 참고1 : 클래스단에 @Transactional(readOnly = true) ---> 해당 클래스 안에 모든 메소드는 읽기 전용으로 설정
//                 트랜잭션이 필요한 메소드에 별도로 @Transactional 애노테이션을 추가하여 오버라이딩하여 사용
// ------------------------------------------------------------------------------------------------------------
//      5). 참고2 : 트랜잭션을 사용하는 메소드가 많은 경우 반대로 클래스단에 @Transactional 을 설정하고
//                 트랜잭션을 사용하지 않는 메소드 (조회) 에 @Transactional(readOnly = true) 을 주어 성능 향상 도모
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService implements UserDetailsService {
    //TODO 2021.01.09 - 8.회원가입 리팩토링 및 테스트
    //     기존 Controller 에서 사용하는 의존성을 Service Layer 로 이동
    //     Controller 의 의존성을 AccountService 만을 받게 변경
    private final AccountRepository accountRepository;
    //private final JavaMailSender javaMailSender;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    //TODO 2021.01.09 - 8.회원가입 리팩토링 및 테스트
    //     Controller Layer 에서 사용할 메소드만을 public 접근제어자로 공개
    //
    //TODO 2021.01.11 12.회원가입 가입 완료 후 자동 로그인
    //     자동 로그인 기능을 구현하기 위해 반환 타입을 Account 객체로 변경
    //     AccountController 에서 회원가입 로직을 처리한 후 Account 객체를 반환받아
    //     login(Account account) [자동 로그인] 로직 실행
    @Transactional
    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        //TODO 이메일 전송에 필요한 토큰 발행
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    //TODO 2021.01.09 - 8.회원가입 리팩토링 및 테스트
    //     리팩토링 : 회원가입을 담당하는 기능을 추출하여 메소드로 분리
    //     Controller Layer 에서 알고있지 않아도 됨으로 private 접근제 어자로 비공개
    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.createAccount(signUpForm.getNickname(),
                signUpForm.getEmail(), passwordEncoder.encode(signUpForm.getPassword()));

        //TODO 뷰에서 받은 데이터 저장
        Account newAccount = accountRepository.save(account);
        return newAccount;
    }

    //TODO 2021.01.09 - 8.회원가입 리팩토링 및 테스트
    //     리팩토링 : 이메일 전송을 담당하는 기능을 추출하여 메소드로 분리
    //     Controller Layer 에서 알고있지 않아도 됨으로 private 접근제어자로 비공개
    // --------------------------------------------------------------------------------------------
    //TODO 2021.01.25 47. MimeMessage 전송하기, EmailService 추상화
    //                 1. EmailService 로 추상화된 객체에 IOC 컨테이너를 통해 구현체를 주입받아 사용
    //                 2. @Profile("local") 환경과 @Profile("dev") 환경에따라 해당 클라언트 코드를 수정하지
    //                    않고 사용 가능
    public void sendSignUpConfirmEmail(Account newAccount) {
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디올래 서비스를 이용하려면 링크를 클릭하세요");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setTo(newAccount.getEmail());
        emailMessage.setSubject("스터디올래, 회원 가입 인증");
        emailMessage.setMessage(message);

        emailService.sendEmail(emailMessage);
    }

    //TODO 2021.01.11 12.회원가입 가입 완료 후 자동 로그인
    //     1. 자동 로그인 비즈니스 로직
    // --------------------------------------------------------------------------------------------
    //     2021.01.13 17.현재 인증된 사용자 정보 참조
    //     1. 인증 토큰 발행 첫째 매개변수가 principal 정보
    //     2. 기존 인증 토큰을 발행할때 기존 account.getNickname() -> new UserAccount(account) 로 변경
    //     3. 스프링 시큐리티와 도메인의 Account 정보를 연동한 어뎁터 객체를 principal 로 사용
    public void login(Account account) {
        //TODO 2021.01.11 12.회원가입 가입 완료 후 자동 로그인
        //     1. 본래는 AuthenticationManager 가 하는 일을 비즈니스 로직에서 구현 - 결과는 동일
        //     2. 이와 같이 사용하는 이유는 아래 정석적으로 인증하는 방법에 명시
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                //account.getNickname(),
                account.getPassword(),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);

        //TODO 2021.01.11 12.회원가입 가입 완료 후 자동 로그인
        //     1. 정석적으로 인증하는 방법
        //      1). AuthenticationManager 사용
        //      2). 폼에서 받은 데이터를 넣어 인증된 객체를 토큰에 넣어줘야된다.
        //     2. 정석적으로 인증하는 방법을 사용하지 못하는 이유 :
        //      1). password 를 평문으로 입력해야하는데 데이터베이스에 password 를 평문으로 저장하지 않는다.
        //      2). 웹 브라우저에서 password 평문을 받는 경우도 있지만 그렇지 않는 경우도 있기 때문에 사용 불가능
//        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken(
//                username, password);
//        Authentication authenticate = authenticationManager.authenticate(token);
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(authenticate);

    }

    //TODO 2021.01.14 로그인 / 로그아웃
    //     1. 로그인할때 인증할때 데이터베이스에 있는 데이터를 기반으로 데이터를 처리해야하기 때문에
    //        UserDetailsService implements 하고
    //        public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException 구현
    //     2. 로직
    //      1). 입력받은 emailOrNickname ( 현재 프로젝트에서 로그인할때 사용하는 value )
    //      2). 데이터베이스에서 emailOrNickname 으로 유저 정보를 검색
    //       -. 이메일로 조회하고 해당 유저 정보가 없다면
    //        >. 닉네임으로 조회
    //         /. 조회된 정보가 없다면 -> throw new UsernameNotFoundException(emailOrNickname) 반환
    //         /. 조회된 정보가 있다면 principal 반환 -> return new UserAccount(account)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if(account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if(account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return new UserAccount(account);
    }


    //TODO 2021.01.15 - 23.Open EntityManager (or Session) In View 필터
    //     1. 해당 도메인 객체의 필드 변경을 Controller Layer 에서 Service Layer 로 이전하고
    //        해당 메소드에 @Transactional 애노테이션을 추가하여 메소드 실행시 트랜잭션 상태를 유지하도록 변경
    @Transactional
    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    //TODO 2021.01.16 25.프로필 수정
    //     1. 폼에서 받은 객체의 데이터로 현재 유저 정보의 데이터 업데이트
    //     2. account 는 현재 준영속(detach) 상태임으로 도메인 객체(account) 만 수정하면
    //        실제 데이터베이스는 업데이트 되지 않음
    //      1). Transactional 선언
    //      2). 영속성 컨텍스트에서 해당 객체를 관리 하지 않기 때문에 변경감지가 동작하지 않음
    //     3. 해당 도메인 객체의 필드 값으로 accountRepository 를 통해 조회(영속성 컨텍스트에 넣은) 후에
    //        값을 변경하면 변경감지 동작
    @Transactional
    public void updateProfile(Account account, Profile profile) {
        //TODO 2021.01.17 31.ModelMapper 적용
        //     1. ModelMapper 에 map 메소드 사용
        //      1). Source 에 있는 값을 Destination 으로 복사해준다.
        //      2). Source : profile, Destination : account
        //     2. 사용 방법
        //      1). modelMapper.map(profile, account);
        //     3. 기존 코드 제거
        modelMapper.map(profile, account);
        accountRepository.save(account);
    }

    //TODO 2021.01.17 28. 패스워드 수정
    //     1. 패스워드 변경은 AccountService Layer 로 위임하여 처리
    //     2. 준영속 상태의 객체의 값을 변경한 뒤 AccountRepository - save 를 호출하여 merge 처리
    //     3. 패스워드를 데이터베이스에 저장할때는 반드시 PasswordEncoder 를 사용하여 해싱한 값 저장
    @Transactional
    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    //TODO 2021.01.17 28. 패스워드 수정
    //     1. 강의 외 패스워드 업데이트 로직 구현
    //     2. 구현 목적 : 
    //      1). 현재 패스워드와 새 패스워드의 값이 동일하면 저장하지 않고 
    //          사용자에게 알림 메시지를 전달하는 방식을 채택
    //     3. 구현 방법 : 
    //      1). 데이터베이스에 저장된 비밀번호는 평문과 해싱된 값을 해싱하면 같은지 비교 가능한 
    //          PasswordEncoder 의 matches 메소드를 활용
    //      2). 두 비밀번호가 같으면 false 를 반환하여 메시지 출력과 뷰랜더링에 활용        
    @Transactional
    public boolean updatePasswordMyLogic(Account account, String newPassword) {
        if (isEqualsCurrentPassword(account.getPassword(), newPassword)) {
            return false;
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        return true;
    }

    //TODO 2021.01.17 28. 패스워드 수정
    //     1. 강의 외 패스워드 업데이트 로직 구현 ( 검증 로직 )
    //     2. 해당 로직은 메소드 명으로 어떤 일을 하는지 나타내 가독성을 높이기위해 별도의 메소드로 추출
    //     3. PasswordEncoder 의 matches 메소드를 이용하여 데이터베이스의 패스워드와 현재 패스워드가 동일한지 비교
    private boolean isEqualsCurrentPassword(String currentPassword, String newPassword) {
        return passwordEncoder.matches(newPassword, currentPassword);
    }

    //TODO 2021.01.17 30.알림 설정
    @Transactional
    public void updateNotifications(Account account, Notifications notifications) {
        //TODO 2021.01.17 31.ModelMapper 적용
        //     1. ModelMapper 에 map 메소드 사용
        //      1). Source 에 있는 값을 Destination 으로 복사해준다.
        //      2). Source : notifications, Destination : account
        //     2. 사용 방법
        //      1). modelMapper.map(notifications, account);
        //     3. 기존 코드 제거
        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }

    //TODO 2021.01.17 32.닉네임 수정
    //     1. 닉네임을 업데이트 한후에 login(account) 호출
    //     2. 이유
    //      1). login(account) 을 호출해주지 않으면 인증된 객체 ( principal) 은 이전 상태의 값을 가지고 있기 때문
    //      2). 서비스를 이용할 때 url 에 nickname 이 포함되는 경우나
    //          nickname 으로 유저를 조회하는 경우에 장애 발생
    @Transactional
    public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
        login(account);
    }

    @Transactional
    public void sendLoginLink(Account account) {
        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("LinkName", "스터디올래 로그인하기");
        context.setVariable("message", "로그인하려면 링크를 클릭하세요");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setTo(account.getEmail());
        emailMessage.setSubject("스터디올래, 로그인 링크");
        emailMessage.setMessage(message);

        emailService.sendEmail(emailMessage);
    }

    //TODO 2021.01.20 37.관심 주제 조회
    //     1. 해당 유저가 입력한 태그를 모두 조회@Transactional
    public List<String> getTags(Account account) {
//        Account findAccount = accountRepository.findById(account.getId())
//                .orElseThrow(() -> new IllegalStateException(""));
        System.out.println("=======================================================");
        Account findAccount = accountRepository.findAccountTagFetchJoinTag(account.getId());
        System.out.println("=======================================================");
        return findAccount.getAccountTags().stream()
                .map(r -> r.getTag().getTitle())
                .collect(Collectors.toList());
    }

    @Transactional
    public void addTag(Account account, Tag tag) {
        Account findAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new NoSuchElementException(""));

        AccountTag accountTag = AccountTag.createAccountTag(account, tag);

        findAccount.addAccountTag(accountTag);
    }

    //TODO 2021.01.20 38.관심 주제 삭제
    //     1. 해당 태그로 조회하여 결과가 없으면 문제가 있는 것
    //     2. 조회된 결과를 삭제
    //      -. 유저에 포함된 AccountTag 를 삭제
    @Transactional
    public void removeTag(Account account, Tag tag) {
//        Account findAccount = accountRepository.findById(account.getId())
//                .orElseThrow(() -> new NoSuchElementException(""));
        Account findAccount = accountRepository.findAccountTagAccountIdAndTagTitle(account.getId(), tag.getTitle());

        findAccount.removeTag(tag.getTitle());
    }

    @Transactional
    public void addAccountZone(Account account, Zone zone) {
        Account findAccount = accountRepository.findAccountZoneLeftJoinFetch(account.getId());
        findAccount.addAccountZone(AccountZone.createAccountZone(account, zone));
    }

    @Transactional
    public void removeAccountZone(Account account, Zone zone) {
        Account findAccount = accountRepository.findAccountZoneJoinFetch(account.getId(), zone.getId());
        findAccount.removeAccountZone(zone.getId());
    }

    public List<String> getZones(Account account) {
        Account findAccount = accountRepository.findAccountZoneLeftJoinFetch(account.getId());
        return findAccount.getAccountZones().stream()
                .map(z -> zoneString(z.getZone()))
                .collect(Collectors.toList());
    }

    public String zoneString(Zone zone) {
        return String.format("{}({}/{})",
                zone.getCity(),
                zone.getLocalNameOfCity(),
                zone.getProvince());
    }

}
