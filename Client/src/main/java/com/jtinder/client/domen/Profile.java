package com.jtinder.client.domen;

import lombok.*;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Profile {
    private Long userChatId;
    private String name;
    private Sex sex;
    private String description;
    private Set<Sex> findSex;
}
