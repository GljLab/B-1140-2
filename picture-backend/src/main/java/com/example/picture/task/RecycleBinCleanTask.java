package com.example.picture.task;

import com.example.picture.service.PictureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RecycleBinCleanTask {

    private static final Logger log = LoggerFactory.getLogger(RecycleBinCleanTask.class);

    @Autowired
    private PictureService pictureService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredPictures() {
        log.info("开始清理回收站过期图片");
        try {
            pictureService.cleanExpiredPictures();
            log.info("回收站过期图片清理完成");
        } catch (Exception e) {
            log.error("清理回收站过期图片失败", e);
        }
    }
}
