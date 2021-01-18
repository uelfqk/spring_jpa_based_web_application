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

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountTagService {
    private final AccountTagRepository accountTagRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void addTag(Account account, TagForm tagForm) {
        if(isEmptyTags(tagForm.getTagTitle())) {
            AccountTag accountTag = AccountTag.createAccountTag(account, Tag.createTag(tagForm.getTagTitle()));
            accountTagRepository.save(accountTag);
        }
    }

    public Set<AccountTag> getTags(Account account) {
        return accountTagRepository.findAccountTags(account.getId());
//        Optional<Account> findAccount = accountRepository.findById(account.getId());
//        return findAccount.orElseThrow(() -> new NoSuchElementException("없다.")).getAccountTags();
    }

    public boolean removeTag(Account account, TagForm tagForm) {
        if(isEmptyTags(tagForm.getTagTitle())) {
            AccountTag accountTag = AccountTag.createAccountTag(account, Tag.createTag(tagForm.getTagTitle()));
            accountTagRepository.delete(accountTag);
            return true;
        }
        return false;
    }

    private boolean isEmptyTags(String title) {
        return accountTagRepository.findTag(title).isEmpty();
    }
}
