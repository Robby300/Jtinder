package com.jtinder.client.domain;

import lombok.*;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Profile {
    private Long userId;
    private String password;
    private String name;
    private Sex sex;
    private String description;
    private Set<Sex> findSex;
}
