package com.studyolle.study;

import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.StudyManager;
import com.studyolle.domain.StudyMember;
import com.studyolle.study.form.StudyForm;
import com.studyolle.study.form.StudyMembersDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;

    public Study createNewStudy(Account account, StudyForm studyForm) {
        Study study = new Study();
        modelMapper.map(studyForm, study);
        StudyManager studyManager = StudyManager.createStudyManager(account);
        study.addStudyManager(studyManager);
        return studyRepository.save(study);
    }

    public StudyMembersDto findMembers(String path) {

        StudyMembersDto studyMembersDto = new StudyMembersDto();
        // 스터디 찾고  일
        Study study = studyRepository.findByPath(path);
        // 매니저 찾고  다
        List<Account> managers = studyRepository.findManagers(study.getId()).stream()
                .map(r -> r.getManager())
                .collect(Collectors.toList());
        // 맴버 찾고    다
        List<Account> members = studyRepository.findMembers(study.getId()).stream()
                .map(r -> r.getMember())
                .collect(Collectors.toList());
        // 지역정보 찾고 다
        // 태그정보 찾고 다

        studyMembersDto.setPath(path);
        studyMembersDto.setTitle(study.getTitle());
        studyMembersDto.setPublished(study.isPublished());
        studyMembersDto.setClosed(study.isClosed());
        studyMembersDto.setRecruiting(study.isRecruiting());
        studyMembersDto.setShortDescription(study.getShortDescription());
        studyMembersDto.setFullDescription(study.getFullDescription());
        studyMembersDto.setTags(new ArrayList<>());
        studyMembersDto.setZones(new ArrayList<>());
        studyMembersDto.setManagers(managers);
        studyMembersDto.setMembers(members);

        return studyMembersDto;
    }
}
