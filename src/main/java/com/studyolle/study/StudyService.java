package com.studyolle.study;

import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.StudyManager;
import com.studyolle.study.form.StudyForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
