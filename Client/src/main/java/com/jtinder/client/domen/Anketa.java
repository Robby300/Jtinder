package com.jtinder.client.domen;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Anketa {

    private long id;
    private long userId;
    private List<Anketa> weLike;
    private List<Anketa> usLike;
}
