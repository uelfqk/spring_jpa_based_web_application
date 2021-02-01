package com.studyolle.study.form;

import com.studyolle.domain.Zone;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class ZoneForm {
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
