package com.udongindie.udong.service;

import com.udongindie.udong.entity.Files;
import com.udongindie.udong.repository.FilesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FilesService {

    private final FilesRepository filesRepository;

    public Long saveFile(MultipartFile imgFile){
        String oriImgName = imgFile.getOriginalFilename();

        UUID uuid = UUID.randomUUID();
        Files files = new Files();
        files.setOriginName(oriImgName);
        files.setSaveName(uuid + "_" + oriImgName);
        File saveFile = new File(files.getFilePath(), files.getSaveName());
        try {
            imgFile.transferTo(saveFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Files saved = filesRepository.save(files);
        return saved.getIdx();
    }
}
