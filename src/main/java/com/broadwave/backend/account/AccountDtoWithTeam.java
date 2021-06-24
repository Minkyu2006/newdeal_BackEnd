package com.broadwave.backend.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author InSeok
 * Date : 2019-03-29
 * Time : 10:31
 * Remark :
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDtoWithTeam {
    private String userid;
    private String username;
    private String email;
    private AccountRole role;
    private String teamcode;
    private String teamname;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role.getDesc();
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public String getTeamcode() {
        return teamcode;
    }

    public void setTeamcode(String teamcode) {
        this.teamcode = teamcode;
    }

    public String getTeamname() {
        return teamname;
    }

    public void setTeamname(String teamname) {
        this.teamname = teamname;
    }
}
