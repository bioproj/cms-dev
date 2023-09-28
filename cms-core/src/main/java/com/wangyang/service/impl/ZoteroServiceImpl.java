package com.wangyang.service.impl;

import com.gimranov.libzotero.HttpHeaders;
import com.gimranov.libzotero.LibraryType;
import com.gimranov.libzotero.SearchQuery;
import com.gimranov.libzotero.ZoteroService;
import com.gimranov.libzotero.model.Creator;
import com.gimranov.libzotero.model.Item;
import com.gimranov.libzotero.model.ObjectVersions;
import com.gimranov.libzotero.model.Tag;
import com.wangyang.common.BaseResponse;
import com.wangyang.common.CmsConst;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.dto.ArticleTagsDto;
import com.wangyang.pojo.entity.Collection;
import com.wangyang.pojo.entity.Literature;
import com.wangyang.pojo.entity.Tags;
import com.wangyang.pojo.entity.Task;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.enums.TaskStatus;
import com.wangyang.pojo.enums.TaskType;
import com.wangyang.repository.base.ContentRepository;
import com.wangyang.service.ICollectionService;
import com.wangyang.service.ILiteratureService;
import com.wangyang.service.ITaskService;
import com.wangyang.service.IZoteroService;
import com.wangyang.util.AuthorizationUtil;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

@Service
public class ZoteroServiceImpl implements IZoteroService {

    @Autowired
    ILiteratureService literatureService;

    @Autowired
    ICollectionService collectionService;

    @Autowired
    ITaskService taskService;
    @Autowired
    private  ThreadPoolTaskExecutor executorService;

    @Autowired
    ContentRepository<Content> contentContentRepository;

    @Override
    public Task importLiterature(int userId) {
        int activeCount = executorService.getActiveCount();
        Task task = taskService.findByENName(TaskType.LITERATURE, CmsConst.ZOTERO_LITERATURE);
        if(task==null){
            task = new Task();
            task.setTaskType(TaskType.LITERATURE);
            task.setEnName( CmsConst.ZOTERO_LITERATURE);
            task  = taskService.save(task);
        }else {
            if(task.status== TaskStatus.RUNNING && activeCount>0){
                throw new ObjectException("任务已经运行！！！");
            }else {
                task.setStatus(TaskStatus.RUNNING);
                task = taskService.save(task);
            }
        }
//        FutureTask<Boolean> futureTask=null;
//        try {
//            Task finalTask = task;
//            futureTask=new FutureTask<>(() -> {
//                importLiterature(userId, finalTask);
//                return true;
//            });
//            executorService.execute(futureTask);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            if(futureTask!=null){
//                System.out.println(futureTask.isDone());
//            }
//        }

//
//        Task finalTask = task;
        Task finalTask = task;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                importLiterature(userId, finalTask);
//                literatureService.generateHtml(userId);
            }
        });

        return task;
    }
    @Override
    public Task importCollection(int userId) {
        int activeCount = executorService.getActiveCount();

        Task task = taskService.findByENName(TaskType.LITERATURE, CmsConst.ZOTERO_COLLECTION);
        if(task==null){
            task = new Task();
            task.setTaskType(TaskType.LITERATURE);
            task.setEnName( CmsConst.ZOTERO_COLLECTION);
            task  = taskService.save(task);
        }else {
            if(task.status== TaskStatus.RUNNING&& activeCount>0){
                throw new ObjectException("任务已经运行！！！");
            }else {
                task.setStatus(TaskStatus.RUNNING);
                task = taskService.save(task);
            }
        }


        Task finalTask = task;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                importCollection(userId, finalTask);
            }
        });
        return task;
    }

    @Async
    @Override
    public void importLiterature(Integer userId, Task task)  {
        List<Collection> collections = collectionService.listAll();
        if(collections.size()==0){
            throw new ObjectException("请先导入分类！！！");
        }
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();

                            Request request = original.newBuilder()
                                    .header(HttpHeaders.AUTHORIZATION,HttpHeaders.AUTHORIZATION_BEARER_X + "anXNFXA8ng0ri04DIAz99Vdd")
                                    .header(HttpHeaders.ZOTERO_API_VERSION,"3")
                                    .method(original.method(), original.body())
                                    .build();
                            return chain.proceed(request);
                        }
                    })

                    .build();
            Retrofit retrofit  = new Retrofit.Builder()
                    .baseUrl("https://api.zotero.org")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            ZoteroService zoteroService = retrofit.create(ZoteroService.class);
//        Map map = new HashMap<>();
//        retrofit2.Call<Map<String, String>> collectionsVersion = zoteroService.getCollectionsVersion(LibraryType.USER, Long.valueOf("8927145"), null);
//        Map<String, String> stringMap = collectionsVersion.execute().body();

            SearchQuery searchQueryVersion = new SearchQuery();
            searchQueryVersion.put("itemType","journalArticle");
            Call<ObjectVersions> itemVersions = zoteroService.getItemVersions(LibraryType.USER, Long.valueOf("8927145"), searchQueryVersion,null);
            ObjectVersions objectVersions = itemVersions.execute().body();
            int size = objectVersions.size();
            int num;
            if(size%100==0){
                num = size/100;
            }else {
                num =Integer.valueOf(size/100)+1;
            }
            List<Item> allItem = new ArrayList<>();
            for (int i=0;i<num;i++){
                SearchQuery searchQuery = new SearchQuery();
                searchQuery.put("limit",100);
                searchQuery.put("start",i*100);
                searchQuery.put("sort","title");

                searchQuery.put("itemType","journalArticle");
                Call<List<Item>> items = zoteroService.getItems(LibraryType.USER, Long.valueOf("8927145"), searchQuery,null);
                List<Item> itemList = items.execute().body();
                allItem.addAll(itemList);
            }

            Map<String, Collection> collectionMap = ServiceUtil.convertToMap(collections, Collection::getKey);

            Set<String> findTags = new HashSet<>();
            List<ArticleTagsDto> articleTagsDtos = new ArrayList<>();
            List<Literature> literatureList = new ArrayList<>();
            for (int i=0;i<allItem.size();i++){
                Item item = allItem.get(i);

                List<String> collectionNames = item.getData().getCollections();
                if(collectionNames.size()>0){
//                    String name = collectionNames.get(0);
                    for (String name :collectionNames){
                        Literature literature= new Literature();
                        literature.setTitle(item.getData().getTitle());
                        literature.setKey(item.getKey());
                        literature.setZoteroKey(item.getKey());
                        literature.setTemplateName(CmsConst.DEFAULT_LITERATURE_TEMPLATE);
                        literature.setUserId(userId);
                        if(item.getData().getUrl().startsWith("http")){
                            literature.setUrl(item.getData().getUrl());
                        }else {
                            literature.setUrl(null);
                        }

                        literature.setOriginalContent(item.getData().getAbstractNote());
                        String dateStr = item.getData().getDate();
                        Date date = null;
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            date = sdf.parse(dateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        literature.setPublishDate(date);
                        List<Creator> creators = item.getData().getCreators();

                        List<Tag> zoteroTags = item.getData().getTags();
                        for(Tag tag :zoteroTags){
                            findTags.add(tag.getTag());
                            articleTagsDtos.add(new ArticleTagsDto(tag.getTag(),literature.getKey()));
                        }

                        if(collectionMap.containsKey(name)){
                            Collection collection = collectionMap.get(name);
                            literature.setCategoryId(collection.getId());

                        }else {
                            literature.setCategoryId(-1);
                        }
                        literature.setPath("html/literature");
                        literature.setViewName(item.getKey());
                        literatureList.add(literature);
                    }
                }else {
                    Literature literature= new Literature();
                    literature.setTitle(item.getData().getTitle());
                    literature.setKey(item.getKey());
                    literature.setZoteroKey(item.getKey());
                    literature.setUrl(item.getData().getUrl());
                    literature.setTemplateName(CmsConst.DEFAULT_LITERATURE_TEMPLATE);
                    literature.setUserId(userId);
                    literature.setOriginalContent(item.getData().getAbstractNote());
                    literature.setCategoryId(-1);
                    literature.setPath("html/literature");
                    literature.setViewName(item.getKey());
                    literatureList.add(literature);
                }



            }



//            literatureService.deleteAll();
            List<Literature> literature = literatureService.listAll();
            Set<String> dbLiterature = ServiceUtil.fetchProperty(literature, Literature::getTitle);
            Set<String> findLiterature = ServiceUtil.fetchProperty(literatureList, Literature::getTitle);
            Set<String> findLiterature2 = new HashSet<>(findLiterature);
            findLiterature.removeAll(dbLiterature);

            List<Literature> needSave = literatureList.stream().filter(item -> findLiterature.contains(item.getTitle())).collect(Collectors.toList());
            List<Literature> literatures = literatureService.saveAll(needSave);
            literatureService.generateHtml(literatures);

            dbLiterature.removeAll(findLiterature2);
            List<Literature> needRemove = literature.stream().filter(item -> dbLiterature.contains(item.getTitle())).collect(Collectors.toList());

            literatureService.deleteAll(needRemove);



        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            task.setStatus(TaskStatus.FINISH);
            taskService.save(task);
        }
    }


    @Async
    @Override
    public void importCollection(Integer userId, Task task)  {

        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();

                            Request request = original.newBuilder()
                                    .header(HttpHeaders.AUTHORIZATION,HttpHeaders.AUTHORIZATION_BEARER_X + "anXNFXA8ng0ri04DIAz99Vdd")
                                    .header(HttpHeaders.ZOTERO_API_VERSION,"3")
                                    .method(original.method(), original.body())
                                    .build();
                            return chain.proceed(request);
                        }
                    })

                    .build();
            Retrofit retrofit  = new Retrofit.Builder()
                    .baseUrl("https://api.zotero.org")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            ZoteroService zoteroService = retrofit.create(ZoteroService.class);
//            Map map = new HashMap<>();
//            retrofit2.Call<Map<String, String>> collectionsVersion = zoteroService.getCollectionsVersion(LibraryType.USER, Long.valueOf("8927145"), null);
//            Map<String, String> stringMap = collectionsVersion.execute().body();
            SearchQuery searchQuery = new SearchQuery();

            searchQuery.put("sort","title");

            searchQuery.put("itemType","journalArticle");

            Call<List<com.gimranov.libzotero.model.Collection>> zoteroCollections = zoteroService.getCollections(LibraryType.USER, Long.valueOf("8927145"),searchQuery, null);
            List<com.gimranov.libzotero.model.Collection> zoteroCollection = zoteroCollections.execute().body();
            List<Collection> collections = new ArrayList<>();
            for (int i=0;i<zoteroCollection.size();i++){
                int id = i+1;
                com.gimranov.libzotero.model.Collection item = zoteroCollection.get(i);
                Collection collection = new Collection();
//                collection.setId(id);
                collection.setKey(item.getKey());
                collection.setName(item.getData().getName());
                collection.setVersion(item.getVersion());
                collection.setParentKey(item.getData().getParentCollection());
                collection.setPath("html/collections");
                collection.setViewName(item.getData().getName());
                collection.setTemplateName(CmsConst.DEFAULT_LITERATURE_CATEGORY_TEMPLATE);
                collections.add(collection);

            }


            List<Collection> dbcoll = collectionService.listAll();
            Set<String> dbCollections = ServiceUtil.fetchProperty(dbcoll, Collection::getName);
            Set<String> findCollections = ServiceUtil.fetchProperty(collections, Collection::getName);
            Set<String> findCollections2 = new HashSet<>(findCollections);

//            collectionService.deleteAll();
//            collectionService.saveAll(collections);
            findCollections.removeAll(dbCollections);

            List<Collection> needSave = collections.stream().filter(item -> findCollections.contains(item.getName())).collect(Collectors.toList());
            List<Collection> saveCollections = collectionService.saveAll(needSave);

            Map<String, Collection> collectionMap = ServiceUtil.convertToMap( collectionService.listAll(), Collection::getKey);
            for (Collection collection : collections){
                if(collectionMap.containsKey(collection.getParentKey())){
                    Integer id = collectionMap.get(collection.getParentKey()).getId();
                    collection.setParentId(id);
                }
            }
            collectionService.saveAll(saveCollections);


            dbCollections.removeAll(findCollections2);
            List<Collection> needRemove = dbcoll.stream().filter(item -> dbCollections.contains(item.getName())).collect(Collectors.toList());
            for (Collection collection: needRemove){
                List<Content> contents = contentContentRepository.findAll(new Specification<Content>() {
                    @Override
                    public Predicate toPredicate(Root<Content> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        return query.where(criteriaBuilder.equal(root.get("categoryId"),collection.getId())).getRestriction();
                    }
                });
                contentContentRepository.deleteAll(contents);
            }

            collectionService.deleteAll(needRemove);


            task.setStatus(TaskStatus.FINISH);
            taskService.save(task);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
