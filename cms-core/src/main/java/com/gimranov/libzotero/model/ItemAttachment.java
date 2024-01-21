package com.gimranov.libzotero.model;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemAttachment {
    private String key;
    private String title;
    private  String parentItem;
}
