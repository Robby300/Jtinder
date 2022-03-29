package com.jtinder.application.domen;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usr")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "userChatId"
)
public class User {

    @Id
    @Column(name = "user_chat_id")
    private Long userChatId;
    @Column(unique = true)
    private String name;

    private Sex sex;
    private String description;


    private Sex findSex;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_relationships",
            joinColumns = {@JoinColumn(name = "user_chat_id")},
            inverseJoinColumns = {@JoinColumn(name = "like_id")}
    )
    private Set<User> weLike = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_relationships",
            joinColumns = {@JoinColumn(name = "like_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_chat_id")}
    )
    private Set<User> usLike;

}
