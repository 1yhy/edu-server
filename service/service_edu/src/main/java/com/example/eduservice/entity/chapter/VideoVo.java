package com.example.eduservice.entity.chapter;

import lombok.Data;

@Data
public class VideoVo {
    private String id;
    private String title;
    private String videoSourceId;
    private Float duration;
    private Boolean isFree;
    private Integer sort;
}
