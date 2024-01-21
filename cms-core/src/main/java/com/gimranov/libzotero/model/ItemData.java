
/*
 * Copyright (C) 2014 Avram Lyon (ajlyon@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gimranov.libzotero.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class ItemData implements Identifiable {

    private String key;

    private String itemType;

    private int version;
    private  String parentItem;
    private String title;
    
    private List<Creator> creators = new ArrayList<>();
    private List<String> collections;

    private String abstractNote;
    
    private String series;
    
    private String seriesNumber;
    
    private String volume;
    
    private String numberOfVolumes;
    
    private String edition;
    
    private String place;
    
    private String publisher;
    
    private String date;
    
    private String numPages;
    
    private String language;
    
    private String ISBN;
    
    private String shortTitle;
    
    private String url;
    
    private String accessDate;
    
    private String archive;
    
    private String archiveLocation;
    
    private String libraryCatalog;
    
    private String callNumber;
    
    private String rights;
    
    private String extra;
    
    private List<Tag> tags = new ArrayList<>();

    @Override
    public String getIdentifier() {
        return key;
    }

    public String getKey() {
        return key;
    }

    public String getItemType() {
        return itemType;
    }

    public int getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }

    public List<Creator> getCreators() {
        return creators;
    }

    public String getAbstractNote() {
        return abstractNote;
    }

    public String getSeries() {
        return series;
    }

    public String getSeriesNumber() {
        return seriesNumber;
    }

    public String getVolume() {
        return volume;
    }

    public String getNumberOfVolumes() {
        return numberOfVolumes;
    }

    public String getEdition() {
        return edition;
    }

    public String getPlace() {
        return place;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDate() {
        return date;
    }

    public String getNumPages() {
        return numPages;
    }

    public String getLanguage() {
        return language;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public String getUrl() {
        return url;
    }

    public String getAccessDate() {
        return accessDate;
    }

    public String getArchive() {
        return archive;
    }

    public String getArchiveLocation() {
        return archiveLocation;
    }

    public String getLibraryCatalog() {
        return libraryCatalog;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public String getRights() {
        return rights;
    }

    public String getExtra() {
        return extra;
    }

    public List<Tag> getTags() {
        return tags;
    }
}
