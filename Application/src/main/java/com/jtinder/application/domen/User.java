package com.jtinder.application.domen;

import lombok.*;

import javax.persistence.*;
import java.util.List;

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
    private Sex sex;
    private String description;
    private Sex findSex;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "welike")
    private List<User> weLike;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "uslike")
    private List<User> usLike;
}
