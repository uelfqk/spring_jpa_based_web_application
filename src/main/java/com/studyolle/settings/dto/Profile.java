package com.studyolle.settings.dto;

import com.studyolle.domain.Account;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Profile {
    //TODO 프로필 정보 ( 자개소개 )
    private String bio;

    //TODO 웹사이트 URL
    private String url;

    //TODO 직업
    private String occupation;

    //TODO 주거지
    private String location;

    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }

    //TODO 2021.01.16 25.프로필 수정
    //     1. 스프링 MVC 가 @ModelAttribute 로 데이터를 받아오려 할때 먼저 인스턴스를 만들고 Setter 를 이용해 데이터를 넣는다.
    //        이때 기본생성자가 없다면 파라미터를 받는 생성자를 사용하게되는데 이때 Account account 값은 모르는 상태임으로
    //        객체 생성에 실패하게된다. - NullPointException 발생
    //     2. 기본생성자를 하나 만들어주면 @ModelAttribute 가 데이터 바인딩에 성공하게 된다.
    //     3. 아래와 같이 기본 생성자를 만들어주거나 Lombok 을 사용하면 @NoArgsConstructor 를 사용
    public Profile() {
    }
}
