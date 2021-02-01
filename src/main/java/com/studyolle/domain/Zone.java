package com.studyolle.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return Objects.equals(getId(), zone.getId()) &&
                Objects.equals(getCity(), zone.getCity()) &&
                Objects.equals(getLocalNameOfCity(), zone.getLocalNameOfCity()) &&
                Objects.equals(getProvince(), zone.getProvince());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCity(), getLocalNameOfCity(), getProvince());
    }

    @Override
    public String toString() {
        return String.format("%s(%s)/%s", city, localNameOfCity, province);
    }
}
