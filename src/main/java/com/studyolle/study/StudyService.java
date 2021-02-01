package com.studyolle.study;

import com.studyolle.domain.*;
import com.studyolle.study.form.StudyDescriptionForm;
import com.studyolle.study.form.StudyForm;
import com.studyolle.study.form.StudyMembersDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;

    private final EntityManager em;

    public Study createNewStudy(Account account, StudyForm studyForm) {
        Study study = modelMapper.map(studyForm, Study.class);

        StudyAccount studyAccount = StudyAccount.createStudyAccount(account);
        study.addStudyAccount(studyAccount);

        return studyRepository.save(study);
    }

    public Study getStudyToUpdate(Account account, String path) {
        Study study = this.getStudy(path);
//        if(account.isManagerOf(study)) {
//            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
//        }
        return study;
    }

    private Study getStudy(String path) {
        Study study = studyRepository.findByPath(path);
        if(study == null) {
            throw new IllegalArgumentException(path + " 에 해당하는 스터디가 없습니다.");
        }
        return study;
    }

    public Study findMembers(String path) {
        return studyRepository.findStudyAccountsByPath(path);

//        return studyRepository.findStudyAndMembersByPath(path);
    }

    public void updateToDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        modelMapper.map(study, studyDescriptionForm);
    }

    public Study enableBannerImage(String path) {
        Study study = studyRepository.findByPath(path);
        study.setUseBanner(true);

        return study;
    }

    public Study disableBannerImage(String path) {
        Study study = studyRepository.findByPath(path);
        study.setUseBanner(false);

        return study;
    }

    public Study updateBannerImage(String path, String image) {
        Study study = getStudy(path);
        study.setImage(image);
        return study;
    }

    public void addTag(String path, Tag tag) {
        Study study = studyRepository.findStudyTagsByPath(path);

        StudyTag studyTag = StudyTag.createStudyTag(study, tag);
        study.addStudyTag(studyTag);
    }

    public void removeTag(String path, Tag tag) {
        Study study = studyRepository.findStudyTagsByPath(path);

        study.removeStudyTag(tag);
    }

    public void addZone(String path, Zone zone) {
        Study study = studyRepository.findStudyZonesByPath(path);

        StudyZone studyZone = StudyZone.createStudyZone(study, zone);
        study.addStudyZone(studyZone);
    }

    public void removeZone(String path, Zone zone) {
        Study study = studyRepository.findStudyZonesByPath(path);

        study.removeStudyZone(zone);
    }
}
