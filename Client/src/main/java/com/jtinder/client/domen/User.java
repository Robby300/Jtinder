package com.jtinder.client.domen;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {

    private long id;
    private String name;
    private Anketa ownAnketa;

    private List<Anketa> weLike;

    private List<Anketa> usLike;

}
