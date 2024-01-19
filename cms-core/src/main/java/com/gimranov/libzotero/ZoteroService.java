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

package com.gimranov.libzotero;

import com.gimranov.libzotero.model.*;
import com.gimranov.libzotero.model.Tag;
import io.reactivex.rxjava3.core.Observable;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;


import java.util.List;
import java.util.Map;

import static com.gimranov.libzotero.HttpHeaders.IF_MODIFIED_SINCE_VERSION;

public interface ZoteroService {

    public static final String TYPE = "type";
    public static final String SINCE = "since";
    public static final String ID = "id";
    public static final String START = "start";

    /**
     * The set of all items in the library
     *
     * @param searchQuery Search parameters for the result set. May be null to apply no search parameters
     */
    @GET("/{type}/{id}/items")
    Call<List<Item>> getItems(@Path(TYPE) LibraryType type,
                              @Path(ID) long id,
                              @Nullable @QueryMap SearchQuery searchQuery,
                              @Nullable @Header(IF_MODIFIED_SINCE_VERSION) String libraryVersion);


    /**
     * The set of all items in the library
     *
     * @param searchQuery Search parameters for the result set. May be null to apply no search parameters
     */
    @GET("/{type}/{id}/items")
    Observable<Response> getItemsNotAsAList(@Path(TYPE) LibraryType type,
                                            @Path(ID) long id,
                                            @Nullable @QueryMap SearchQuery searchQuery,
                                            @Nullable @Header(IF_MODIFIED_SINCE_VERSION) String libraryVersion);

    @GET("/{type}/{id}/items?format=versions")
    Call<ObjectVersions> getItemVersions(@Path(TYPE) LibraryType type,
                                               @Path(ID) long id,
                                               @Nullable @QueryMap SearchQuery searchQuery,
                                               @Nullable @Header(IF_MODIFIED_SINCE_VERSION) String libraryVersion);

    /**
     * The set of all top-level items in the library
     */
    @GET("/{type}/{id}/items/top")
    Observable<List<Item>> getTopLevelItems(@Path(TYPE) LibraryType type,
                                            @Path(ID) long id,
                                            @Nullable @QueryMap Map searchQuery,
                                            @Nullable @Header(IF_MODIFIED_SINCE_VERSION) String libraryVersion);

    /**
     * The set of items in the trash
     */
    @GET("/{type}/{id}/items/trash")
    Observable<List<Item>> getTrashItems(@Path(TYPE) LibraryType type,
                                         @Path(ID) long id,
                                         @Nullable @QueryMap Map searchQuery);

    /**
     * A specific item in the library
     */
    @GET("/{type}/{id}/items/{itemKey}")
    Observable<Item> getItem(@Path(TYPE) LibraryType type,
                             @Path(ID) long id,
                             @Path("itemKey") String itemKey,
                             @Nullable @Header(IF_MODIFIED_SINCE_VERSION) String itemVersion);

    /**
     * The set of all child items under a specific item
     */
    @GET("/{type}/{id}/items/{itemKey}/children")
    Observable<List<Item>> getItemChildren(@Path(TYPE) LibraryType type,
                                           @Path(ID) long id,
                                           @Path("itemKey") String itemKey);

    /**
     * The set of all tags associated with a specific item
     */
    @GET("/{type}/{id}/items/{itemKey}/tags")
    Observable<Item> getItemTags(@Path(TYPE) LibraryType type,
                                 @Path(ID) long id,
                                 @Path("itemKey") String itemKey);

    /**
     * The set of all tags in the library
     */
    @GET("/{type}/{id}/tags")
    Observable<List<Item>> getTags(@Path(TYPE) LibraryType type,
                                   @Path(ID) long id,
                                   @Nullable @Header(IF_MODIFIED_SINCE_VERSION) String libraryVersion);

    /**
     * The set of tags (i.e., of all types) matching a specific name
     */
    @GET("/{type}/{id}/tags/{tagQuery}")
    Observable<List<Item>> getTagsMatches(@Path(TYPE) LibraryType type,
                                          @Path(ID) long id,
                                          @Path("tagQuery") String urlEncodedTagQuery);

    /**
     * The set of collections in the library
     */
    @GET("/{type}/{id}/collections?limit=100")
    Call<List<Collection>> getCollections(@Path(TYPE) LibraryType type,
                                                @Path(ID) long id,
                                                @Nullable @QueryMap SearchQuery searchQuery,
                                                @Nullable @Header(IF_MODIFIED_SINCE_VERSION) String libraryVersion);
    @GET("/{type}/{id}/collections?format=versions")
    Call<Map<String,String>> getCollectionsVersion(@Path(TYPE) LibraryType type,
                                          @Path(ID) long id,
                                          @Nullable @Header(IF_MODIFIED_SINCE_VERSION) String libraryVersion);

    /**
     * The set of all top-level collections in the library
     */
    @GET("/{type}/{id}/collections/top")
    Observable<List<Collection>> getTopLevelCollections(@Path(TYPE) LibraryType type,
                                                        @Path(ID) long id,
                                                        @Nullable @Header(IF_MODIFIED_SINCE_VERSION) String libraryVersion);

    /**
     * A specific collection in the library
     */
    @GET("/{type}/{id}/collections/{collectionKey}")
    Observable<Collection> getCollection(@Path(TYPE) LibraryType type,
                                         @Path(ID) long id,
                                         @Path("collectionKey") String collectionKey);

    /**
     * The set of subcollections within a specific collection in the library
     */
    @GET("/{type}/{id}/collections/{collectionKey}/collections")
    Observable<List<Collection>> getCollectionSubcollections(@Path(TYPE) LibraryType type,
                                                             @Path(ID) long id,
                                                             @Path("collectionKey") String collectionKey);

    /**
     * The set of all items within a specific collection in the library
     */
    @GET("/{type}/{id}/collections/{collectionKey}/items")
    Observable<List<Item>> getCollectionItems(@Path(TYPE) LibraryType type,
                                              @Path(ID) long id,
                                              @Path("collectionKey") String collectionKey,
                                              @Nullable @QueryMap Map searchQuery,
                                              @Nullable @Header(IF_MODIFIED_SINCE_VERSION) String libraryVersion);

    /**
     * The set of top-level items within a specific collection in the library
     */
    @GET("/{type}/{id}/collections/{collectionKey}/items/top")
    Observable<List<Item>> getCollectionTopLevelitems(@Path(TYPE) LibraryType type,
                                                      @Path(ID) long id,
                                                      @Path("collectionKey") String collectionKey,
                                                      @Nullable @QueryMap Map searchQuery,
                                                      @Nullable @Header(IF_MODIFIED_SINCE_VERSION) String libraryVersion);

    /**
     * The set of tags within a specific collection in the library
     */
    @GET("/{type}/{id}/collections/{collectionKey}/tags")
    Observable<List<Tag>> getCollectionTags(@Path(TYPE) LibraryType type,
                                            @Path(ID) long id,
                                            @Path("collectionKey") String collectionKey);

    /**
     * The set of all saved searches in the library
     */
    @GET("/{type}/{id}/searches")
    Observable<List<Search>> getSearches(@Path(TYPE) LibraryType type,
                                         @Path(ID) long id,
                                         @Nullable @Header(IF_MODIFIED_SINCE_VERSION) String libraryVersion);

    /**
     * A specific saved search in the library
     */
    @GET("/{type}/{id}/searches/{searchKey}")
    Observable<Search> getSearch(@Path(TYPE) LibraryType type,
                                 @Path(ID) long id,
                                 @Path("searchKey") String searchKey);

    /**
     * The set of groups the current API key has access to, including public groups the key owner belongs to even if the
     * key doesn't have explicit permissions for it.
     */
    @GET("/users/{id}/groups")
    Observable<List<Group>> getGroups(@Path(ID) long id);

    /**
     * The privileges of the given API key.
     */
    @GET("/users/{id}/keys/{key}")
    Observable<Key> getPrivileges(@Path(ID) long id,
                                        @Path("key") String key);


    /**
     * The set of all items in the library
     *
     * @param searchQuery Search parameters for the result set. May be null to apply no search parameters
     */
    @GET("/{type}/{id}/deleted")
    Call<ZoteroKeys> getDeleted(@Path(TYPE) LibraryType type,
                              @Path(ID) long id,
                              @Nullable @QueryMap SearchQuery searchQuery,
                              @Nullable @Header(IF_MODIFIED_SINCE_VERSION) String libraryVersion);
}
