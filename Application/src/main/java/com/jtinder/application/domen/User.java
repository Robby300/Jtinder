package com.jtinder.application.domen;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "usr")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String name;
    @Enumerated(EnumType.STRING)
    private Sex sex;
    private String description;

    @Enumerated(EnumType.STRING)
    private Sex findSex;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_relationships",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "like_id")}
    )
    private Set<User> weLike;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_relationships",
            joinColumns = {@JoinColumn(name = "like_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<User> usLike;



}
