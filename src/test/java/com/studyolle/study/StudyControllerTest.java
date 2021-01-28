package com.studyolle.study;

import com.studyolle.domain.Study;
import com.studyolle.domain.StudyManager;
import com.studyolle.domain.StudyMember;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudyControllerTest {

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyManagerRepository studyManagerRepository;

    @Test
    void findStudyAndMembersByPathTest() {

        Study cc = studyRepository.findStudyAndMembersByPath("cc");

    }
}