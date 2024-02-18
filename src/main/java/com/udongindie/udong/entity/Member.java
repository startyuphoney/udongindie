package com.udongindie.udong.entity;

import com.udongindie.udong.enums.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_idx")
    private Long idx;
    private String name;
    private String address;
    private String username;
    private String pwd;
    private LocalDateTime birthDate;
    private String tel;
    private String gender;
    private LocalDateTime joinDate;

    @Enumerated(EnumType.STRING)
    private RoleType role = RoleType.USER;

    @OneToMany(mappedBy = "member")
    private List<Board> board = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Orders> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Answer> answer = new ArrayList<>();






}
