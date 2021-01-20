package com.studyolle.service;

import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import com.studyolle.domain.AccountTag;
import com.studyolle.domain.Tag;
import com.studyolle.repository.AccountTagRepository;
import com.studyolle.settings.form.TagForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountTagService {
    private final AccountTagRepository accountTagRepository;

    @Transactional
    public void addTag(Account account, TagForm tagForm) {
        AccountTag findTag = accountTagRepository.findByTagTitle(tagForm.getTagTitle());

        if(findTag == null) {
            AccountTag accountTag = AccountTag.createAccountTag(account, Tag.createTag(tagForm.getTagTitle()));
            accountTagRepository.save(accountTag);
        }
    }

    public Set<AccountTag> getTags(Account account) {
        return accountTagRepository.findAccountTags(account.getId());
    }

    public boolean removeTag(Account account, TagForm tagForm) {
        AccountTag findTag = accountTagRepository.findByTagTitle(tagForm.getTagTitle());
        if(findTag == null) {
            return false;
        }

        accountTagRepository.delete(findTag);
        return true;
//        if(!isEmptyTags(tagForm.getTagTitle())) {
//            AccountTag accountTag = AccountTag.createAccountTag(account, Tag.createTag(tagForm.getTagTitle()));
//            accountTagRepository.delete(accountTag);
//            return true;
//        }
//        return false;
    }

    private boolean isEmptyTags(String title) {
        return false;
//        List<AccountTag> tag = accountTagRepository.findTagTitle(title);
//        return (tag.size() == 0) ? true : false; //accountTagRepository.findTag(title).isEmpty();
    }

    public List<String> findAllTag() {
        List<AccountTag> result = accountTagRepository.findAll();
        return result.stream().map(r -> r.getTag().getTitle()).collect(Collectors.toList());
    }
}
