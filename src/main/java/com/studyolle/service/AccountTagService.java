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
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountTagService {
    private final AccountTagRepository accountTagRepository;
    private final AccountRepository accountRepository;

    public void addTag(Account account, TagForm tagForm) {
        AccountTag findTag = accountTagRepository.findTag(account.getId(), tagForm.getTagTitle());

        if(findTag == null) {
            AccountTag accountTag = AccountTag.createAccountTag(account, Tag.createTag(tagForm.getTagTitle()));
            accountTagRepository.save(accountTag);
        }
    }

    //TODO 2021.01.20 37.관심 주제 조회
    //     1. 해당 유저가 입력한 태그를 모두 조회
    @Transactional(readOnly = true)
    public List<AccountTag> getTags(Account account) {
        return accountTagRepository.findAccountTags(account.getId());
    }

    //TODO 2021.01.20 38.관심 주제 삭제
    //     1. 해당 태그로 조회하여 결과가 없으면 문제가 있는 것
    //     2. 조회된 결과를 삭제
    //      -. 유저에 포함된 AccountTag 를 삭제
    public boolean removeTag(Account account, TagForm tagForm) {
        AccountTag findTag = accountTagRepository.findTag(account.getId(), tagForm.getTagTitle());
        if(findTag == null) {
            return false;
        }
        accountTagRepository.delete(findTag);
        return true;
    }

    //TODO 2021.01.20 39.관심 주제 자동완성
    //     1. 데이터베이스에 등록되어있는 태그 정보를 모두 조회하여
    //        List<String> 타입으로 변환한 다음 반환 - stream Api 사용
    @Transactional(readOnly = true)
    public List<String> findAllTag() {
        List<AccountTag> result = accountTagRepository.findAll();
        return result.stream().map(r -> r.getTag().getTitle()).collect(Collectors.toList());
    }
}
