package com.jtinder.client.domen;

import lombok.*;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {

    private String name;

    private Sex sex;
    private String description;
    private Sex findSex;

    private Set<User> weLike;

    private Set<User> usLike;
}
