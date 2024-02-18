package com.udongindie.udong.entity;

import com.udongindie.udong.enums.Rating;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Item {

    @Id @GeneratedValue
    @Column(name = "item_idx")
    private Long idx;
    private String name;
    private int price;
    private int stockQuantity;
    private String beginDate;
    private String runningTime;
    private String genre;
    private String theater;
    private LocalDateTime createDate;
    private String imgName;
    private String imgPath;
    private String content;
    @Enumerated(EnumType.STRING)
    private Rating rating;
    private String beginDateInfo;
    private String imgNameDetail;
    private String imgPathDetail;
    private String posterImage;
    private String detailImage;
}
