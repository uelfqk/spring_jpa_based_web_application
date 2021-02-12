package com.studyolle.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.study.form.*;
import com.studyolle.tag.TagRepository;
import com.studyolle.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
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
        createByStudy();

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
        assertThat(form.getShortDescription()).isEqualTo("short");
        assertThat(form.getFullDescription()).isEqualTo("full");
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("소개 수정하기 - 성공")
    void updateStudyDescriptionSuccessTest() throws Exception {
        createByStudy();

        mockMvc.perform(post("/study/study/settings/description")
                    .param("path", "study")
                    .param("shortDescription", "짧은")
                    .param("fullDescription", "긴")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study/settings/description"));

        Study study = studyRepository.findByPath("study");

        assertThat(study).isNotNull();
        assertThat(study.getShortDescription()).isEqualTo("짧은");
        assertThat(study.getFullDescription()).isEqualTo("긴");
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("소개 수정하기 - 실패")
    void updateStudyDescriptionFailTest() throws Exception {
        createByStudy();

        mockMvc.perform(post("/study/study/settings/description")
                .param("path", "study")
                .param("shortDescription", "")
                .param("fullDescription", "긴")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));

        Study study = studyRepository.findByPath("study");

        assertThat(study).isNotNull();
        assertThat(study.getShortDescription()).isEqualTo("short");
        assertThat(study.getFullDescription()).isEqualTo("full");
    }

    @Test
    @WithAccount("youngbin")
    @DisplayName("배너 이미지 설정 보여주기")
    void viewBannerImageTest() throws Exception {
        createByStudy();

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
        createByStudy();

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
        createByStudy();

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
        createByStudy();

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
        createByStudy();

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
        createByStudy();

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
        createByStudy();

        createByStudyTag("스프링");

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
        createByStudy();

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
        createByStudy();

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
        createByStudy();

        createByStudyZone("Seoul", "none");

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

    @Test
    @WithAccount("youngbin")
    @DisplayName("스터디 지역정보 삭제 - 실패")
    void removeZoneFailTest() throws Exception {
        createByStudy();

        createByStudyZone("Seoul", "none");

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName("aaa(bbb)/ccc");

        mockMvc.perform(post("/study/study/settings/zones/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("스터디 설정 보여주기")
    @WithAccount("youngbin")
    void viewStudyTest() throws Exception {
        createByStudy();

        mockMvc.perform(get("/study/study/settings/study"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test @DisplayName("스터디 공개 - 성공")
    @WithAccount("youngbin")
    void publishStudySuccessTest() throws Exception {
        createByStudy();

        mockMvc.perform(post("/study/study/settings/study/publish")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study/settings/study"))
                .andExpect(flash().attributeExists("message"));

        Study study = studyRepository.findByPath("study");

        assertThat(study).isNotNull();
        assertThat(study.isPublished()).isTrue();
        assertThat(study.getPublishedDateTime()).isNotNull();
    }

    @Test @DisplayName("스터디 종료 - 성공")
    @WithAccount("youngbin")
    @Transactional
    void closeStudySuccessTest() throws Exception {
        Study study = createByStudy();
        study.setPublished(true);

        mockMvc.perform(post("/study/study/settings/study/close")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study/settings/study"))
                .andExpect(flash().attributeExists("message"));

        Study findStudy = studyRepository.findByPath("study");

        assertThat(findStudy).isNotNull();
        assertThat(findStudy.isClosed()).isTrue();
        assertThat(findStudy.getClosedDateTime()).isNotNull();
    }

    @Test @DisplayName("스터디 인원 모집 - 성공")
    @WithAccount("youngbin")
    @Transactional
    void startRecruitStudySuccessTest() throws Exception {
        Study study = createByStudy();

        study.setPublished(true);

        mockMvc.perform(post("/study/study/settings/recruit/start")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study/settings/study"))
                .andExpect(flash().attributeExists("message"));

        Study findStudy = studyRepository.findByPath("study");

        assertThat(findStudy).isNotNull();
        assertThat(findStudy.isRecruiting()).isTrue();
        assertThat(findStudy.getRecruitingUpdateDatetime()).isNotNull();
    }

    @Test @DisplayName("스터디 인원 모집 - 성공")
    @WithAccount("youngbin")
    @Transactional
    void stopRecruitStudySuccessTest() throws Exception {
        Study study = createByStudy();

        study.setPublished(true);
        study.setRecruiting(true);

        mockMvc.perform(post("/study/study/settings/recruit/start")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study/settings/study"))
                .andExpect(flash().attributeExists("message"));

        Study findStudy = studyRepository.findByPath("study");

        assertThat(findStudy).isNotNull();
        assertThat(findStudy.isRecruiting()).isTrue();
        assertThat(findStudy.getRecruitingUpdateDatetime()).isNotNull();
    }

    @Test @DisplayName("스터디 경로 수정 - 성공")
    @WithAccount("youngbin")
    void updateStudyPathSuccessTest() throws Exception {
        createByStudy();

        mockMvc.perform(post("/study/study/settings/study/path")
                .param("newPath", "new-study")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/new-study/settings/study"))
                .andExpect(flash().attributeExists("message"));

        Study study = studyRepository.findByPath("new-study");

        assertThat(study).isNotNull();
        assertThat(study.getPath()).isEqualTo("new-study");
    }

    @Test @DisplayName("스터디 경로 수정 - 실패")
    @WithAccount("youngbin")
    void updateStudyPathFailTest() throws Exception {
        createByStudy();

        mockMvc.perform(post("/study/study/settings/study/path")
                .param("newPath","sadhasdnkjqwndlkwqndlk;askhas;lkfhlkqw;roiqffabfa" +
                        "bfakjbjabskjclsabjkasbjaklgjkdbwqjkdbwqkjdbqwkjdbaksjdbsakdbkjsadsa")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("message"));

        Study study = studyRepository.findByPath("study");

        assertThat(study).isNotNull();
        assertThat(study.getPath()).isEqualTo("study");
    }

    @Test @DisplayName("스터디 제목 수정 - 성공")
    @WithAccount("youngbin")
    void updateStudyTitleSuccessTest() throws Exception {
        createByStudy();

        MvcResult result = mockMvc.perform(post("/study/study/settings/study/title")
                .param("newTitle", "스터디 제목 수정")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(redirectedUrl("/study/study/settings/study"))
                .andExpect(flash().attributeExists("message"))
                .andReturn();

        Study study = studyRepository.findByPath("study");

        assertThat(study).isNotNull();
        assertThat(study.getTitle()).isEqualTo("스터디 제목 수정");
    }

    @Test @DisplayName("스터디 제목 수정 - 실패")
    @WithAccount("youngbin")
    void updateStudyTitleFailTest() throws Exception {
        createByStudy();

        MvcResult result = mockMvc.perform(post("/study/study/settings/study/title")
                .param("newTitle", "qwnkrlnwqkldrnqklnfdlkwqnflkwqnflkqnflkanflksanflkafnaknflkanflkafnaklfnkalfafsafaqwqwrqwr")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/study"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("message"))
                .andReturn();

        String message = result.getRequest().getAttribute("message").toString();
        Study requestStudy = (Study)result.getRequest().getAttribute("study");
        Account requestAccount = (Account)result.getRequest().getAttribute("account");

        Study study = studyRepository.findByPath("study");

        Account account = accountRepository.findByNickname("youngbin");

        assertThat(study).isNotNull();
        assertThat(study.getTitle()).isEqualTo("title");
        assertThat(message).isEqualTo("사용할 수 없는 제목입니다.");
        assertThat(requestStudy.getId()).isEqualTo(study.getId());
        assertThat(requestAccount.getId()).isEqualTo(account.getId());
    }

    @Test @DisplayName("스터디 삭제 - 성공")
    @WithAccount("youngbin")
    void removeStudySuccessTest() throws Exception {
        createByStudy();

        mockMvc.perform(post("/study/study/settings/study/remove")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Study study = studyRepository.findByPath("study");

        assertThat(study).isNull();
    }
    
    Study createByStudy() {
        Account account = accountRepository.findByNickname("youngbin");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study");
        studyForm.setTitle("title");
        studyForm.setShortDescription("short");
        studyForm.setFullDescription("full");

        return studyService.createNewStudy(account, studyForm);
    }

    void createByStudyTag(String title) {
        Tag tag = tagRepository.save(Tag.createTag(title));
        studyService.addTag("study", tag);
    }

    void createByStudyZone(String city, String province) {
        Zone zone = zoneRepository.findByCityAndProvince(city, province);
        studyService.addZone("study", zone);
    }
}