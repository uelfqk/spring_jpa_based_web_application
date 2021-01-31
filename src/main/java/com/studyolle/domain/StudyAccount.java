package com.studyolle.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyAccount {

    @Id @GeneratedValue
    @Column(name = "study_account_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public boolean isManager(Account account) {
        return this.account.equals(account);
    }

    public static StudyAccount createStudyAccount(Account account) {
        StudyAccount studyAccount = new StudyAccount();
        studyAccount.setAccount(account);
        return studyAccount;
    }
}
