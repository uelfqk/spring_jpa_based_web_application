package com.studyolle.account;

import com.studyolle.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String email);

    Account findByNickname(String emailOrNickname);

    @Query("select acc from Account acc " +
            "left outer join fetch acc.accountTags at " +
            "left outer join fetch at.tag " +
            "where acc.id = :accountId")
    Account findAccountTagFetchJoinTag(@Param("accountId") Long accountId);

    @Query("select acc from Account acc " +
            "left outer join fetch acc.accountTags at " +
            "left outer join fetch at.tag t " +
            "where acc.id = :accountId and t.title = :tagTitle")
    Account findAccountTagAccountIdAndTagTitle(@Param("accountId") Long accountId, @Param("tagTitle") String tagTitle);
}
