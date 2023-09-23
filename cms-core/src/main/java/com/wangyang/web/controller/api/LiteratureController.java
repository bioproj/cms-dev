package com.wangyang.web.controller.api;

import com.gimranov.libzotero.*;
import com.gimranov.libzotero.model.Item;
import com.gimranov.libzotero.model.ObjectVersions;
import com.google.gson.Gson;
import com.wangyang.common.BaseResponse;
import com.wangyang.common.CmsConst;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.Collection;
import com.wangyang.service.*;
import com.wangyang.util.AuthorizationUtil;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
//import retrofit.RequestInterceptor;
//import retrofit.RestAdapter;
//import retrofit.converter.GsonConverter;
//import rx.Observable;
//import zotero.api.Collection;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/literature")
public class LiteratureController {
    @Autowired
    ILiteratureService literatureService;

    @Autowired
    ICollectionService collectionService;

    @Autowired
    ITemplateService templateService;

    @Autowired
    IComponentsService componentsService;

    @Autowired
    IZoteroService zoteroService;

    @Autowired
    IHtmlService htmlService;

    @PostMapping
    public Literature add(@RequestBody Literature literature){
        Literature saveLiterature = literatureService.add(literature);
        return saveLiterature;
    }

    @GetMapping
    public Page<Literature> list(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable){
        return literatureService.pageBy(pageable);
    }
    @PostMapping("/save/{id}")
    public Literature save(@RequestBody  Literature literatureParam,@PathVariable("id") Integer id){
        Literature literature = literatureService.findById(id);
        BeanUtils.copyProperties(literatureParam,literature,CMSUtils.getNullPropertyNames(literatureParam));
        Literature saveLiterature = literatureService.update(id, literature);
//        Literature saveLiterature = literatureService.save(literature);
        // 需要判断文章模板路径
//        literatureService.checkContentTemplatePath(saveLiterature);

//        ArticleDetailVO articleDetailVO = contentService.convert(content);
//        ArticleDetailVO articleDetailVO = articleService.convert(article);
        return saveLiterature;
    }
    @PostMapping("/update/{id}")
    public Literature update(@RequestBody  Literature literatureParam,@PathVariable("id") Integer id){
        Literature literature = literatureService.findById(id);
        BeanUtils.copyProperties(literatureParam,literature,CMSUtils.getNullPropertyNames(literatureParam));
        Literature saveLiterature = literatureService.update(id, literature);
//        Literature saveLiterature = literatureService.save(literature);
        // 需要判断文章模板路径
        literatureService.checkContentTemplatePath(saveLiterature);

//        ArticleDetailVO articleDetailVO = contentService.convert(content);
//        ArticleDetailVO articleDetailVO = articleService.convert(article);
        htmlService.conventHtml(saveLiterature);
        return saveLiterature;
    }

    @GetMapping("/find/{id}")
    public Literature findById(@PathVariable("id") Integer id){
        return literatureService.findById(id);
    }

    @RequestMapping("/delete/{id}")
    public BaseResponse delete(@PathVariable("id") Integer id){
        Literature literature = literatureService.findById(id);
        literatureService.delete(literature);

        return BaseResponse.ok("Delete id "+id+" menu success!!");
    }

    @GetMapping("/import")
    public Task importData(HttpServletRequest request)  {
        int userId = AuthorizationUtil.getUserId(request);
        Task task = zoteroService.importLiterature(userId);
        return task;
    }
    @GetMapping("/sync")
    public Task syncLiterature(HttpServletRequest request){
        int userId = AuthorizationUtil.getUserId(request);
        Task task = zoteroService.importLiterature(userId);
        return task;

    }
    @GetMapping("/generateHtml")
    public BaseResponse generateHtml(HttpServletRequest request){
        int userId = AuthorizationUtil.getUserId(request);
        literatureService.generateHtml(userId);
        return BaseResponse.ok("success!!");

    }
}
