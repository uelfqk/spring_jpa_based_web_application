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
public class Tag {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    public static Tag createTag(String title) {
        Tag tag = new Tag();
        tag.setTitle(title);
        return tag;
    }
}
