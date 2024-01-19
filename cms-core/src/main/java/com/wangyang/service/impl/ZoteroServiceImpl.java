package com.wangyang.service.impl;

import com.gimranov.libzotero.HttpHeaders;
import com.gimranov.libzotero.LibraryType;
import com.gimranov.libzotero.SearchQuery;
import com.gimranov.libzotero.ZoteroService;
import com.gimranov.libzotero.model.*;
import com.wangyang.common.BaseResponse;
import com.wangyang.common.CmsConst;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.dto.ArticleTagsDto;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.Collection;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.enums.TaskStatus;
import com.wangyang.pojo.enums.TaskType;
import com.wangyang.pojo.enums.TemplateData;
import com.wangyang.pojo.enums.TemplateType;
import com.wangyang.repository.base.ContentRepository;
import com.wangyang.service.*;
import com.wangyang.service.templates.ITemplateService;
import com.wangyang.util.AuthorizationUtil;
import okhttp3.*;
import org.springframework.beans.BeanUtils;
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


    private static  final String ZOTERO_VERSION ="ZOTERO_VERSION";
    @Autowired
    ILiteratureService literatureService;

    @Autowired
    ICollectionService collectionService;

    @Autowired
    ITemplateService templateService;
    @Autowired
    ITaskService taskService;
    @Autowired
    private  ThreadPoolTaskExecutor executorService;

    @Autowired
    ContentRepository<Content> contentContentRepository;

    @Autowired
    ICategoryTemplateService categoryTemplateService;


    @Autowired
    IOptionService optionService;

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
    public ZoteroService getZoteroService(){
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
        return zoteroService;
    }
//    public ObjectVersions listVersion( ZoteroService zoteroService,SearchQuery searchQuery,String since) throws IOException {
//        if(searchQuery==null){
//            searchQuery = new SearchQuery();
//        }
////         = new SearchQuery();
////        searchQueryVersion.put("itemType",itemType);
//        Call<ObjectVersions> itemVersions = zoteroService.getItemVersions(LibraryType.USER, Long.valueOf("8927145"), searchQuery,since);
//        ObjectVersions objectVersions = itemVersions.execute().body();
//        return objectVersions;
//    }
    public List<Item> listByItemType(ZoteroService zoteroService, int size,SearchQuery searchQuery,String since) throws IOException {

//        = objectVersions.size();

        if(searchQuery==null){
            searchQuery = new SearchQuery();
        }

        int num;
        if(size%100==0){
            num = size/100;
        }else {
            num =Integer.valueOf(size/100)+1;
        }
        List<Item> allItem = new ArrayList<>();
        for (int i=0;i<num;i++){
//             = new SearchQuery();
            searchQuery.put("limit",100);
            searchQuery.put("start",i*100);
            searchQuery.put("sort","title");
            searchQuery.put("since",since);
//            searchQuery.put("itemType",itemType);
            Call<List<Item>> items = zoteroService.getItems(LibraryType.USER, Long.valueOf("8927145"),searchQuery,null);
            List<Item> itemList = items.execute().body();
            allItem.addAll(itemList);
        }
        return allItem;
    }

    public ZoteroKeys getDeleted(ZoteroService zoteroService ,String since){

        try {
//            = getZoteroService();
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.put("since",since);
            Call<ZoteroKeys> serviceDeleted = zoteroService.getDeleted(LibraryType.USER, Long.valueOf("8927145"), searchQuery,null);
            ZoteroKeys  zoteroKeys = serviceDeleted.execute().body();
            return zoteroKeys;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }



    @Async
    @Override
    public void importLiterature(Integer userId, Task task)  {
        Option option = optionService.findByOptionalKey(ZOTERO_VERSION).orElse(Option.builder().key(ZOTERO_VERSION).value("0").build());
        ZoteroService zoteroService = getZoteroService();




        List<Collection> collections = collectionService.listAll();
        if(collections.size()==0){
            throw new ObjectException("请先导入分类！！！");
        }

        try {
            String since = option.getValue();
            if(since== null || since.equals("0")){
                since = String.valueOf(0);
                List<Literature> delLiterature = literatureService.listAll();
                literatureService.deleteAll(delLiterature);
            }

//        Map map = new HashMap<>();
//        retrofit2.Call<Map<String, String>> collectionsVersion = zoteroService.getCollectionsVersion(LibraryType.USER, Long.valueOf("8927145"), null);
//        Map<String, String> stringMap = collectionsVersion.execute().body();
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.put("itemType","thesis || journalArticle"); //journalArticle
            searchQuery.put("since",since);
//            ObjectVersions objectVersions = listVersion(zoteroService, searchQuery, since);
            Call<ObjectVersions> itemVersions = zoteroService.getItemVersions(LibraryType.USER, Long.valueOf("8927145"), searchQuery,null);
            retrofit2.Response<ObjectVersions> objectVersionsResponse = itemVersions.execute();
            ObjectVersions objectVersions = objectVersionsResponse.body();
            if(objectVersions!=null || objectVersions.size()!=0){
                List<Item> allItem = listByItemType(zoteroService,objectVersions.size(),searchQuery,since);
//            allItem.addAll(listByItemType("thesis",since));

//            List<Item> thesis = listByItemType("thesis");
//            allItem.addAll(thesis);
//            searchQueryVersion.put("itemType","thesis");
//            itemVersions = zoteroService.getItemVersions(LibraryType.USER, Long.valueOf("8927145"), searchQueryVersion,null);




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
                            literature.setItemType(item.getData().getItemType());
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
                        literature.setItemType(item.getData().getItemType());
                        literature.setOriginalContent(item.getData().getAbstractNote());
                        literature.setCategoryId(-1);
                        literature.setPath("html/literature");
                        literature.setViewName(item.getKey());
                        literatureList.add(literature);
                    }



                }

//            List<Literature> literature = literatureService.listAll();
//            Set<String> dbLiterature = ServiceUtil.fetchProperty(literature, Literature::getTitle);
//            Set<String> findLiterature = ServiceUtil.fetchProperty(literatureList, Literature::getTitle);
////            Set<String> findLiterature2 = new HashSet<>(findLiterature);
//            findLiterature.removeAll(dbLiterature);
//
//            List<Literature> needSave = literatureList.stream().filter(item -> findLiterature.contains(item.getTitle())).collect(Collectors.toList());
                for (Literature literature : literatureList){
                    Literature literatureServiceByKeys = literatureService.findByKeys(literature.getKey());
                    if(literatureServiceByKeys!=null){
//                        literature.setId(literatureServiceByKeys.getId());
                        continue;
                    }
                    Literature save = literatureService.save(literature);
                    literatureService.generateHtml(save);
                }


//            dbLiterature.removeAll(findLiterature2);
//            List<Literature> needRemove = literature.stream().filter(item -> dbLiterature.contains(item.getTitle())).collect(Collectors.toList());
//
//            literatureService.deleteAll(needRemove);



//                if(option.getValue().equals("0")){
                String lastVersion = objectVersionsResponse.headers().get("Last-Modified-Version");
                option.setValue(lastVersion);
                optionService.saveUpdateOption(option);
//                }else{
                ZoteroKeys zoteroKeys = getDeleted(zoteroService,option.getValue());
                List<Literature> delLiterature = literatureService.listByKeys(zoteroKeys.getItems());
                List<Literature> filteredList = delLiterature.stream()
                        .filter(element -> element != null)
                        .collect(Collectors.toList());

                literatureService.deleteAll(filteredList);
//                }
            }


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
                collection.setTemplateData(TemplateData.OTHER);
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
//            for (Collection collection : saveCollections) {
//                Template template = templateService.findByEnName(CmsConst.DEFAULT_LITERATURE_CATEGORY_TEMPLATE);
//                Collection saveCollection = collectionService.save(collection);
//                CategoryTemplate categoryTemplate = new CategoryTemplate(saveCollection.getId(),template.getId(), TemplateType.CATEGORY);
//                categoryTemplateService.save(categoryTemplate);
//            }
                collectionService.saveAll(saveCollections);

//



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
