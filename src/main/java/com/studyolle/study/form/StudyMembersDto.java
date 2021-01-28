package com.studyolle.study.form;

import com.studyolle.account.UserAccount;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class StudyMembersDto {
    private String path;

    private String title;

    private boolean published;

    private boolean closed;

    private boolean recruiting;

    private String shortDescription;

    private String fullDescription;

    private List<Tag> tags;

    private List<Zone> zones;

    private List<Account> managers;

    private List<Account> members;

    public boolean isManager(UserAccount userAccount) {
        return managers.contains(userAccount.getAccount());
    }

    public boolean isMember(UserAccount userAccount) {
        return members.contains(userAccount.getAccount());
    }

    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);
    }
}
