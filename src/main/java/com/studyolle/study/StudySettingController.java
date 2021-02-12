package com.studyolle.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.study.form.*;
import com.studyolle.tag.TagRepository;
import com.studyolle.tag.TagService;
import com.studyolle.zone.ZoneRepository;
import com.studyolle.zone.ZoneService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
public class StudySettingController {
    private final StudyRepository studyRepository;
    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final ZoneRepository zoneRepository;

    private final TagService tagService;
    private final ZoneService zoneService;

    @GetMapping("/description")
    public String showSettings(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("studyDescriptionForm", modelMapper.map(study, StudyDescriptionForm.class));
        return "study/settings/description";
    }

    @PostMapping("/description")
    public String updateStudyDescription(@CurrentUser Account account, @PathVariable String path,
                                         @Valid @ModelAttribute StudyDescriptionForm studyDescriptionForm,
                                         Errors errors, Model model) {
        Study study = studyRepository.findByPath(path);

        if(errors.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            return "study/settings/description";
        }

        studyService.updateToDescription(study, studyDescriptionForm);
        return "redirect:/study/"+ study.getPath() +"/settings/description";
    }

    @GetMapping("/banner")
    public String viewBannerImage(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);

        model.addAttribute("account", account);
        model.addAttribute("study", study);

        return "study/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableBannerImage(@CurrentUser Account account, @PathVariable String path) throws UnsupportedEncodingException {
        Study study = studyService.enableBannerImage(path);
        return "redirect:/study/" + study.getEncodingPath() + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableBannerImage(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.disableBannerImage(path);
        return "redirect:/study/" + study.getPath() + "/settings/banner";
    }

    @PostMapping("/banner")
    public String updateBannerImage(@CurrentUser Account account, @PathVariable String path,
                                    String image, RedirectAttributes attributes) {
        Study study = studyService.updateBannerImage(path, image);
        attributes.addFlashAttribute("message", "스터디 배너 이미지를 수정했습니다.");
        return "redirect:/study/" + study.getPath() + "/settings/banner";
    }

    @GetMapping("/tags")
    public String viewTags(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyRepository.findStudyTagsByPath(path);

        List<String> whitelist = tagService.getTagWhiteList();

        List<String> tags = study.getStudyTags().stream().map(t -> t.getTag().getTitle())
                .collect(Collectors.toList());

        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("tags", tags);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));

        return "study/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTags(@CurrentUser Account account, @PathVariable String path,
                                  @RequestBody TagForm tagForm) {
        String tagTitle = tagForm.getTagTitle();

        Tag tag = tagService.getTag(tagTitle);

        studyService.addTag(path, tag);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTags(@CurrentUser Account account, @PathVariable String path,
                                     @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title);

        if(tag == null) {
            return ResponseEntity.badRequest().build();
        }

        studyService.removeTag(path, tag);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String viewZones(@CurrentUser Account account, Model model,
                            @PathVariable String path) throws JsonProcessingException {
        Study study = studyRepository.findStudyZonesByPath(path);

        List<String> zones = study.getStudyZones().stream()
                .map(z -> z.getZone().toString())
                .collect(Collectors.toList());

        List<String> whitelist = zoneService.getZoneWhiteList();

        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("zones", zones);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));

        return "study/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentUser Account account, @PathVariable String path,
                                  @RequestBody ZoneForm zoneForm) {

        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCity(), zoneForm.getProvince());
        if(zone == null) {
            ResponseEntity.badRequest().build();
        }

        studyService.addZone(path, zone);

        return ResponseEntity.ok().build();
    }

    @PostMapping("zones/remove")
    public ResponseEntity removeZone(@CurrentUser Account account, @PathVariable String path,
                                     @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCity(), zoneForm.getProvince());

        if(zone == null) {
            return ResponseEntity.badRequest().build();
        }

        studyService.removeZone(path, zone);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/study")
    public String showStudy(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute("account", account);
        model.addAttribute("study", study);
        return "study/settings/study";
    }

    @PostMapping("/study/publish")
    public String publishStudy(@CurrentUser Account account, @PathVariable String path,
                               RedirectAttributes attributes) throws UnsupportedEncodingException {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.publishStudy(study);
        attributes.addFlashAttribute("message", "스터디를 공개하였습니다.");
        return getSettingsStudyReturn(study.getEncodingPath());
    }

    @PostMapping("/study/close")
    public String closeStudy(@CurrentUser Account account, @PathVariable String path,
                             RedirectAttributes attributes) throws UnsupportedEncodingException {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.closeStudy(study);
        attributes.addFlashAttribute("message", "스터디를 종료하였습니다.");
        return getSettingsStudyReturn(study.getEncodingPath());
    }

    @PostMapping("/recruit/start")
    public String startRecruitStudy(@CurrentUser Account account, @PathVariable String path,
                                    Model model, RedirectAttributes attributes) throws UnsupportedEncodingException {
        Study study = studyRepository.findByPath(path);

        if(!study.canRecruiting()) {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            model.addAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "study/settings/study";
        }

        studyService.startRecruit(study);
        attributes.addFlashAttribute("message", "스터디 인원 모집을 시작했습니다.");
        return getSettingsStudyReturn(study.getEncodingPath());
    }

    @PostMapping("/recruit/stop")
    public String stopRecruitStudy(@CurrentUser Account account, @PathVariable String path,
                                   Model model, RedirectAttributes attributes) throws UnsupportedEncodingException {
        Study study = studyRepository.findByPath(path);

        if(!study.canRecruiting()) {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            model.addAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "study/settings/study";
        }

        studyService.stopRecruit(study);
        attributes.addFlashAttribute("message", "스터디 인원 모집을 종료했습니다.");
        return getSettingsStudyReturn(study.getEncodingPath());
    }

    @PostMapping("/study/path")
    public String updateStudyPath(@CurrentUser Account account, @PathVariable String path,
                                  @Valid @ModelAttribute StudyPathForm studyPathForm, Errors errors,
                                  Model model, RedirectAttributes attributes) throws UnsupportedEncodingException {
        Study study = studyService.getStudyToUpdate(account, path);

        if(errors.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            model.addAttribute("message", "사용할 수 없는 경로 입니다.");
            return "study/settings/study";
        }

        studyService.updateStudyPath(study, studyPathForm.getNewPath());
        attributes.addFlashAttribute("message", "스터디 경로가 수정되었습니다.");

        return getSettingsStudyReturn(study.getEncodingPath());
    }

    @PostMapping("/study/title")
    public String updateStudyTitle(@CurrentUser Account account, @PathVariable String path,
                                   @Valid @ModelAttribute StudyTitleForm studyTitleForm, Errors errors,
                                   Model model, RedirectAttributes attributes) throws UnsupportedEncodingException {

        Study study = studyService.getStudyToUpdate(account, path);

        if(errors.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            model.addAttribute("message", "사용할 수 없는 제목입니다.");
            return "study/settings/study";
        }

        studyService.updateStudyTitle(study, studyTitleForm.getNewTitle());
        attributes.addFlashAttribute("message", "스터디 제목이 수정되었습니다.");
        return getSettingsStudyReturn(study.getEncodingPath());
    }

    @PostMapping("/study/remove")
    public String removeStudy(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.removeStudy(study);
        return "redirect:/";
    }

    private String getSettingsStudyReturn(String path) {
        return "redirect:/study/" + path + "/settings/study";
    }
}
