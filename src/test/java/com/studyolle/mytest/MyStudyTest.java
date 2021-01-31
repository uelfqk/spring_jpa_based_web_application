package com.studyolle.mytest;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.StudyManager;
import com.studyolle.study.StudyService;
import com.studyolle.study.form.StudyForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SpringBootTest
public class MyStudyTest {

    @Autowired AccountRepository accountRepository;
    @Autowired StudyService studyService;
    @PersistenceContext EntityManager em;


    @WithAccount("youngbin")
    @Test
    @Transactional
    void 스터디_생성_테스트() {
        // given

        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("bb");
        studyForm.setTitle("타이틀");
        studyForm.setShortDescription("짧은");
        studyForm.setFullDescription("긴");

        Study study = studyService.createNewStudy(account, studyForm);

        em.flush();
        em.clear();

        Study findStudy = em.find(Study.class, study.getId());
        List<StudyManager> studyManagers = findStudy.getStudyManagers();
    }
}
