package com.wangyang.pojo.vo;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrawingAttachmentVo {
    private String name;
    private String originContent;
    private MultipartFile file;
    private String svg;
    private ImageType imageType;
}
