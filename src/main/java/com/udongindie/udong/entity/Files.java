package com.udongindie.udong.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Files {

    @Id @GeneratedValue
    @Column(name = "file_idx")
    private Long idx;
    private String originName;
    private String saveName;
    private String filePath = System.getProperty("user.dir") + "/src/main/resources/static/files/";
}
