package com.jtinder.application.domen;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;

    private String description;
    private Sex findSex;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_relationships",
        joinColumns = { @JoinColumn(name = "user_id") },
        inverseJoinColumns = { @JoinColumn(name = "like_id")}
    )
    private List<User> weLike;

/*    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "user_sex", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Sex> sex;*/
}
