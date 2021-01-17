package com.studyolle.settings.form;

import com.studyolle.domain.Account;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
public class Profile {
    //TODO 프로필 정보 ( 자개소개 )
    @Length(max = 35)
    private String bio;

    //TODO 웹사이트 URL
    @Length(max = 50)
    private String url;

    //TODO 직업
    @Length(max = 50)
    private String occupation;

    //TODO 주거지
    @Length(max = 50)
    private String location;

    private String profileImage;

    //TODO 2021.01.17 31.ModelMapper 적용
    //     1. Profile 객체는 스프링 빈이 아니기 때문에 ModelMapper 를 주입 받을 수 없는 상황
    //     2. 객체의 생성과 프로퍼티 매핑을 해당 객체에서 하지 않고 Controller Layer 로 위임
    //     3. 해당 생성자 제거, 해당 생성자를 제거함으로 기본 생성자가 활성화 됨으로 @NoArgsConstructor 제거
    //     4. Code
    // ------------------------------------------------------------------------------------
    // public Profile(Account account) {
    //     this.bio = account.getBio();
    //     this.url = account.getUrl();
    //     this.occupation = account.getOccupation();
    //     this.location = account.getLocation();
    //     this.profileImage = account.getProfileImage();
    // }

    //TODO 2021.01.16 25.프로필 수정
    //     1. 스프링 MVC 가 @ModelAttribute 로 데이터를 받아오려 할때 먼저 인스턴스를 만들고 Setter 를 이용해 데이터를 넣는다.
    //        이때 기본생성자가 없다면 파라미터를 받는 생성자를 사용하게되는데 이때 Account account 값은 모르는 상태임으로
    //        객체 생성에 실패하게된다. - NullPointException 발생
    //     2. 기본생성자를 하나 만들어주면 @ModelAttribute 가 데이터 바인딩에 성공하게 된다.
    //     3. 아래와 같이 기본 생성자를 만들어주거나 Lombok 을 사용하면 @NoArgsConstructor 를 사용
    public Profile() {
    }
}
