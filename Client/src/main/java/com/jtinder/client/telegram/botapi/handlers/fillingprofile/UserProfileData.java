package com.jtinder.client.telegram.botapi.handlers.fillingprofile;

import com.jtinder.client.domen.Sex;
import com.jtinder.client.domen.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileData {
    private String name;

    private Sex sex;
    private String description;
    private Sex findSex;

    private Set<User> weLike;

    private Set<User> usLike;
}
