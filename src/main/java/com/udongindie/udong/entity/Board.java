package com.udongindie.udong.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Board {

    @Id @GeneratedValue
    @JoinColumn(name = "board_idx")
    private Long idx;
    private String title;
    private String content;
    private LocalDateTime createDate;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member member;

    @OneToMany(mappedBy = "board")
    private List<Answer> answer = new ArrayList<>();


}
