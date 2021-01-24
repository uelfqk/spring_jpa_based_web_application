package com.studyolle.settings.form;

import com.studyolle.domain.Zone;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class ZoneForm {

    //TODO 2021.01.24 42.지역 정보 도메인
    //     1. View 에서 전달 되는 지역 정보
    //     2. 구성
    //      1). Seoul(서울특별시)/none
    //      2). 해당 문자열을 Zone 도메인의 상태에 맞춰 파싱
    //      3). getCity(), getLocalNameOfCity(), getProvince()
    @NotBlank
    private String zoneName;

    public String getCity() {
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    public String getLocalNameOfCity() {
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    }

    public String getProvince() {
        return zoneName.substring(zoneName.indexOf("/") + 1);
    }

    public Zone getZone() {
        return Zone.createZone(
                getCity(),
                getLocalNameOfCity(),
                getProvince()
        );
    }
}
