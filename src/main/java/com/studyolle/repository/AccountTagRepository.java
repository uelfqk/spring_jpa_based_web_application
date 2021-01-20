package com.studyolle.repository;

import com.studyolle.domain.AccountTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface AccountTagRepository extends JpaRepository<AccountTag, Long> {
    AccountTag findByTagTitle(String tagTitle);

    @Query("select a from AccountTag a " +
            "join fetch a.account acc " +
            "join fetch a.tag t " +
            "where acc.id = :accountId and t.title = :tagTitle")
    List<AccountTag> findTag(@Param("accountId") Long accountId, @Param("tagTitle") String tagTitle);

    @Query("select a from AccountTag a " +
            "join fetch a.tag t " +
            "where t.title = :tagTitle")
    AccountTag findTagTitle(@Param("tagTitle") String tagTitle);

    @Query("select a from AccountTag a join fetch a.tag t")
    List<AccountTag> findAllTags();

    @Query("select a from AccountTag a " +
            "join fetch a.account acc " +
            "join fetch a.tag " +
            "where acc.id = :accountId")
    Set<AccountTag> findAccountTags(@Param("accountId") Long accountId);
}
