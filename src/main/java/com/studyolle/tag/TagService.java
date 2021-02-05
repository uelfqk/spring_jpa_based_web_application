package com.studyolle.tag;

import com.studyolle.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Tag getTag(String title) {
        Tag tag = tagRepository.findByTitle(title);
        if(tag == null) {
            tag = tagRepository.save(Tag.createTag(title));
        }
        return tag;
    }

    public List<String> getTagWhiteList() {
        return tagRepository.findAll().stream()
                .map(t -> t.getTitle())
                .collect(Collectors.toList());
    }
}
