package com.wangyang.pojo.entity;

import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.pojo.enums.AttachmentStoreType;
import com.wangyang.pojo.enums.AttachmentType;
import com.wangyang.pojo.vo.ImageType;
import lombok.Data;
import javax.persistence.*;
@Entity
@Data
public class Attachment extends BaseEntity {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
    private String name;
    private String path;
    private String fileKey;
    private String suffix;
    private String attachmentNumber;
    private Long size;
    @Column(name = "attachment_type")
    private AttachmentStoreType type;
    private String mediaType;
    private Integer width;
    private Integer height;
    private String thumbPath;
    private String latex;
    @Column( columnDefinition = "longtext")
    private String formatContent;
    @Column( columnDefinition = "longtext")
    private String originContent;
    private String renderType;
    private Integer objId;
    @Column(name = "attachment_type_2")
    @Enumerated(EnumType.STRING)
    private AttachmentType attachmentType;
    @Enumerated(EnumType.STRING)
    private ImageType imageType;
}
