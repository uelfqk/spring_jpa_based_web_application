package com.studyolle.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Zone {

    @Id @GeneratedValue
    @Column(name = "zone_id")
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String localNameOfCity;

    private String province;

    public static Zone createZone(String city, String localNameOfCity, String province) {
        Zone zone = new Zone();
        zone.setCity(city);
        zone.setLocalNameOfCity(localNameOfCity);
        zone.setProvince(province);
        return zone;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)/%s", city, localNameOfCity, province);
    }
}
