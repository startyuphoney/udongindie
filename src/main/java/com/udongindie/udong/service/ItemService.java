package com.udongindie.udong.service;

import com.udongindie.udong.entity.Item;
import com.udongindie.udong.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final FilesService filesService;

    /**
     * 공연 등록
     */
    public void saveItem(Item item, MultipartFile imgFile, MultipartFile imgFile2) throws Exception {

        Long posterIdx = filesService.saveFile(imgFile);
        Long detailIdx = filesService.saveFile(imgFile2);

        item.setPosterImage(posterIdx.toString());
        item.setDetailImage(detailIdx.toString());

        item.setCreateDate(LocalDateTime.now());

        itemRepository.save(item);
        log.info("item Saved");
    }

    /**
     * 가장 마지막에 등록된 공연 찾기
     */
    public Item latestItem(){

        List<Item> items = itemRepository.findAll();

        Long maxIdx = 0L;
        Item latestItem = null;

        for (Item item : items) {
            if (item.getIdx() > maxIdx){
                maxIdx = item.getIdx();
                latestItem = item;
            }
        }

        return latestItem;
    }

    /**
     * 공연 수정
     */
//    @Transactional
    public void modify(Long idx, Item item) {
        Item originItem = itemRepository.findById(idx).get();
        originItem.setBeginDate(item.getBeginDate());
        originItem.setContent(item.getContent());
        originItem.setImgName(item.getImgName());
        originItem.setImgPath(item.getImgPath());
        originItem.setGenre(item.getGenre());
        originItem.setName(item.getName());
        originItem.setPrice(item.getPrice());
        originItem.setRating(item.getRating());
        originItem.setTheater(item.getTheater());
        originItem.setRunningTime(item.getRunningTime());
        itemRepository.save(originItem);
        log.info("item modified");
    }


}
