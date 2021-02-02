package com.studyolle.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.repository.TagRepository;
import com.studyolle.study.form.StudyDescriptionForm;
import com.studyolle.study.form.StudyForm;
import com.studyolle.study.form.TagForm;
import com.studyolle.study.form.ZoneForm;
import com.studyolle.zone.ZoneRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudySettingControllerTest {

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    StudyService studyService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    void clear() {
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("소개 폼 보여주기")
    void showFormTest() throws Exception {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        studyService.createNewStudy(account, studyForm);

        MvcResult result = mockMvc.perform(get("/study/study/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andReturn();

        MockHttpServletRequest request = result.getRequest();
        StudyDescriptionForm form = (StudyDescriptionForm) request.getAttribute("studyDescriptionForm");

        assertThat(form).isNotNull();
        assertThat(form.getPath()).isEqualTo("study");
        assertThat(form.getTitle()).isEqualTo("title");
        assertThat(form.getShortDescription()).isEqualTo("short");
        assertThat(form.getFullDescription()).isEqualTo("full");
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("소개 수정하기 - 성공")
    void updateStudyDescriptionSuccessTest() throws Exception {

    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("소개 수정하기 - 실패")
    void updateStudyDescriptionFailTest() throws Exception {

    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("배너 이미지 설정 보여주기")
    void viewBannerImageTest() throws Exception {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        studyService.createNewStudy(account, studyForm);

        mockMvc.perform(get("/study/study/settings/banner"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/banner"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("배너 이미지 사용으로 변경")
    void enableBannerImageTest() throws Exception {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        studyService.createNewStudy(account, studyForm);

        mockMvc.perform(post("/study/study/settings/banner/enable")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study/settings/banner"));

        Study study = studyRepository.findByPath("study");

        assertThat(study).isNotNull();
        assertThat(study.isUseBanner()).isTrue();
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("배너 이미지 사용안함으로 변경")
    void disableBannerImageTest() throws Exception {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        studyService.createNewStudy(account, studyForm);

        mockMvc.perform(post("/study/study/settings/banner/disable")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study/settings/banner"));

        Study study = studyRepository.findByPath("study");

        assertThat(study).isNotNull();
        assertThat(study.isUseBanner()).isFalse();
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("배너 이미지 변경")
    void updateBannerImageTest() throws Exception {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        studyService.createNewStudy(account, studyForm);

        mockMvc.perform(post("/study/study/settings/banner")
                    .param("image","abcdefg")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study/settings/banner"));

        Study study = studyRepository.findByPath("study");

        assertThat(study).isNotNull();
        assertThat(study.getImage()).isEqualTo("abcdefg");
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("스터디 태그 보여주기")
    void viewTagsTest() throws Exception {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        studyService.createNewStudy(account, studyForm);

        mockMvc.perform(get("/study/study/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whitelist"));
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("스터디 태그 추가")
    void addTagTest() throws Exception {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        studyService.createNewStudy(account, studyForm);

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("스프링");

        mockMvc.perform(post("/study/study/settings/tags/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tagForm))
                    .with(csrf()))
                .andExpect(status().isOk());

        Study study = studyRepository.findStudyTagsByPath("study");

        Tag tag = tagRepository.findByTitle("스프링");

        assertThat(study).isNotNull();
        assertThat(study.getStudyTags().size()).isEqualTo(1);
        assertThat(tag).isNotNull();
        assertThat(tag.getTitle()).isEqualTo("스프링");
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("스터디 태그 삭제")
    void removeTagTest() throws Exception {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        studyService.createNewStudy(account, studyForm);

        Tag tag = tagRepository.save(Tag.createTag("스프링"));

        studyService.addTag("study", tag);

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("스프링");

        mockMvc.perform(post("/study/study/settings/tags/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Study study = studyRepository.findStudyTagsByPath("study");

        Tag findTag = tagRepository.findByTitle("스프링");

        assertThat(study).isNotNull();
        assertThat(study.getStudyTags().size()).isEqualTo(0);
        assertThat(findTag).isNotNull();
        assertThat(findTag.getTitle()).isEqualTo("스프링");
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("스터디 지역정보 보여주기")
    void viewZoneTest() throws Exception {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        studyService.createNewStudy(account, studyForm);

        MvcResult result = mockMvc.perform(get("/study/study/settings/zones"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/zones"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(model().attributeExists("whitelist"))
                .andReturn();
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("스터디 지역정보 추가")
    void addZoneTest() throws Exception {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        studyService.createNewStudy(account, studyForm);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName("Seoul(서울특별시)/none");

        mockMvc.perform(post("/study/study/settings/zones/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Study study = studyRepository.findStudyZonesByPath("study");

        Zone zone = zoneRepository.findByCityAndProvince("Seoul", "none");

        assertThat(study).isNotNull();
        assertThat(study.getStudyZones().size()).isEqualTo(1);
        assertThat(zone).isNotNull();
        assertThat(zone.getCity()).isEqualTo("Seoul");
        assertThat(zone.getLocalNameOfCity()).isEqualTo("서울특별시");
        assertThat(zone.getProvince()).isEqualTo("none");
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("스터디 지역정보 삭제")
    void removeZoneTest() throws Exception {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        studyService.createNewStudy(account, studyForm);

        Zone zone = zoneRepository.findByCityAndProvince("Seoul", "none");
        studyService.addZone("study", zone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName("Seoul(서울특별시)/none");

        mockMvc.perform(post("/study/study/settings/zones/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Study study = studyRepository.findStudyZonesByPath("study");

        Zone findZone = zoneRepository.findByCityAndProvince("Seoul", "none");

        assertThat(study).isNotNull();
        assertThat(study.getStudyZones().size()).isEqualTo(0);
        assertThat(findZone).isNotNull();
        assertThat(findZone.getCity()).isEqualTo("Seoul");
        assertThat(findZone.getLocalNameOfCity()).isEqualTo("서울특별시");
        assertThat(findZone.getProvince()).isEqualTo("none");
    }
}