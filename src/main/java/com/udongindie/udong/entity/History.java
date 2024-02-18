package com.udongindie.udong.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class History {

    @Id @GeneratedValue
    @Column(name = "history_idx")
    private Long idx;
    private String title;
    private String performYear;
    private String posterImg;
}
