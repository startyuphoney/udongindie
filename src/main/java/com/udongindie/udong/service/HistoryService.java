package com.udongindie.udong.service;

import com.udongindie.udong.entity.History;
import com.udongindie.udong.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final FilesService filesService;

    public List<History> getList() {
        return historyRepository.findAll();
    }

    /**
     * 히스토리 추가
     * @param history
     * @param imgFile
     */
    public void saveHistory(History history, MultipartFile imgFile) {

        Long posterIdx = filesService.saveFile(imgFile);
        history.setPosterImg(posterIdx.toString());

        historyRepository.save(history);

    }
}
