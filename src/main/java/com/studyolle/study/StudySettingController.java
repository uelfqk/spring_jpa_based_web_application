package com.studyolle.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.repository.TagRepository;
import com.studyolle.study.form.StudyDescriptionForm;
import com.studyolle.study.form.StudyMembersDto;
import com.studyolle.study.form.TagForm;
import com.studyolle.study.form.ZoneForm;
import com.studyolle.zone.ZoneRepository;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

        List<Tag> allTag = tagRepository.findAll();

        List<String> whitelist = allTag.stream().map(t -> t.getTitle()).collect(Collectors.toList());

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

        Tag tag = tagRepository.findByTitle(tagTitle);

        if (tag == null) {
            tag = tagRepository.save(Tag.createTag(tagTitle));
        }

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

        List<String> whitelist = zoneRepository.findAll().stream()
                .map(z -> z.toString())
                .collect(Collectors.toList());


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
            zone = zoneRepository.save(Zone.createZone(zoneForm.getCity(),
                    zoneForm.getLocalNameOfCity(), zoneForm.getProvince()));
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
}
