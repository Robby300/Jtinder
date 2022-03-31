package com.jtinder.client.domen;

import lombok.*;

import java.util.List;
import java.util.Set;


@Getter
@Setter
@ToString
public class User {
    private Profile profile;
    private List<Profile> profileList;
    private int Page;

    public User(Long userId) {
        this.profile = new Profile();
        profile.setUserId(userId);
    }
}
