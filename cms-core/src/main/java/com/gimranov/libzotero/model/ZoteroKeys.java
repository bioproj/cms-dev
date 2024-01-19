package com.gimranov.libzotero.model;


import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ZoteroKeys {
    private List<String> collections;
    private Set<String> items;
}
