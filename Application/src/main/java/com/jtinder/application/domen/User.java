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
    private Anketa ownAnketa;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "welike")
    private List<Anketa> weLike;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "uslike")
    private List<Anketa> usLike;

}
