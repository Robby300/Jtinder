package com.jtinder.application.domen;

import javax.persistence.*;
import java.util.List;

@Table(name = "anketa")
public class Anketa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long userId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "welike")
    private List<Anketa> weLike;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "uslike")
    private List<Anketa> usLike;
}
