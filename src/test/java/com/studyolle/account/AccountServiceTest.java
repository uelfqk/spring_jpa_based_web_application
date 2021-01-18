package com.studyolle.account;

import com.studyolle.domain.Account;
import com.studyolle.domain.AccountTag;
import com.studyolle.domain.Tag;
import com.studyolle.repository.AccountTagRepository;
import com.studyolle.service.AccountTagService;
import com.studyolle.settings.form.TagForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountTagRepository accountTagRepository;

    @Autowired
    AccountTagService accountTagService;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("Tag 저장 테스트")
    @Transactional
    void Tag_저장_테스트() throws Exception {
        Account account = Account.createAccount("asfasf", "aaaa@email", "123456789");
        accountRepository.save(account);

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("Spring");

        accountTagService.addTag(account, tagForm);

        Account findAccount = accountRepository.findByEmail("aaaa@email.com");

        AccountTag accountTag = accountTagRepository.findByTagTitle(tagForm.getTagTitle());

        Account account1 = accountTag.getAccount();
        Tag tag1 = accountTag.getTag();

    }

    @Test
    @DisplayName("컬랙션 조회 테스트")
    @Transactional
    void 컬랙션_조회_테스트() throws Exception {
        // given
        Account account = Account.createAccount("asfasf", "aaaa@email", "123456789");
        accountRepository.save(account);

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("Spring");
        accountTagService.addTag(account, tagForm);

        TagForm tagForm2 = new TagForm();
        tagForm2.setTagTitle("Spring1");
        accountTagService.addTag(account, tagForm2);

        // when
        List<AccountTag> result = accountTagRepository.findTag(tagForm.getTagTitle());

        // then

        Assertions.assertThat(result.size()).isNotEqualTo(0);
    }

    @Test
    @DisplayName("findAccountTags Test")
    void findAccountTagsTest() throws Exception {
        // given
        Account account = Account.createAccount("asfasf", "aaaa@email", "123456789");
        accountRepository.save(account);

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("Spring");
        accountTagService.addTag(account, tagForm);

        TagForm tagForm2 = new TagForm();
        tagForm2.setTagTitle("Spring1");
        accountTagService.addTag(account, tagForm2);

        Account account1 = Account.createAccount("asdsad", "asdsada@email", "123456789");
        accountRepository.save(account1);

        TagForm tagForm3 = new TagForm();
        tagForm3.setTagTitle("Spring3");
        accountTagService.addTag(account1, tagForm3);

        TagForm tagForm4 = new TagForm();
        tagForm4.setTagTitle("Spring4");
        accountTagService.addTag(account1, tagForm4);

        Set<AccountTag> tags = accountTagService.getTags(account);

        // when
        List<String> result = tags.stream().map(accountTag -> accountTag.getTag().getTitle())
                .collect(Collectors.toList());

        result.forEach(r -> System.out.println("r = " + r));

        Assertions.assertThat(result.size()).isEqualTo(2);
    }
}