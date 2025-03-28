package com.wangyang.web.controller.api;

import cn.hutool.core.bean.BeanUtil;
import com.wangyang.common.BaseResponse;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.params.LiteratureParam;
import com.wangyang.pojo.vo.LiteratureVo;
import com.wangyang.service.*;
import com.wangyang.service.templates.IComponentsService;
import com.wangyang.service.templates.ITemplateService;
import com.wangyang.util.AuthorizationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
//import retrofit.RequestInterceptor;
//import retrofit.RestAdapter;
//import retrofit.converter.GsonConverter;
//import rx.Observable;
//import zotero.api.Collection;

import javax.servlet.http.HttpServletRequest;

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
    public Literature save(@RequestBody LiteratureParam literatureParam,
                           @PathVariable("id") Integer id,
                           @RequestParam(required = false,defaultValue = "false") Boolean previewParse){
        Literature literature = literatureService.findById(id);
        BeanUtils.copyProperties(literatureParam,literature,CMSUtils.getNullPropertyNames(literatureParam));
        Literature saveLiterature = literatureService.update(id, literature);

        Literature literatureView = BeanUtil.copyProperties(saveLiterature, Literature.class);

        if(previewParse){
            htmlService.previewParse(literatureView);
        }
//        Literature saveLiterature = literatureService.save(literature);
        // 需要判断文章模板路径
//        literatureService.checkContentTemplatePath(saveLiterature);

//        ArticleDetailVO articleDetailVO = contentService.convert(content);
//        ArticleDetailVO articleDetailVO = articleService.convert(article);
        return literatureView;
    }
    @PostMapping("/update/{id}")
    public LiteratureVo update(@RequestBody  LiteratureParam literatureParam,@PathVariable("id") Integer id){
        Literature literature = literatureService.findById(id);
        BeanUtils.copyProperties(literatureParam,literature,CMSUtils.getNullPropertyNames(literatureParam));
        LiteratureVo saveLiterature = literatureService.update(id, literature,literatureParam.getTagIds());
//        Literature saveLiterature = literatureService.save(literature);
        // 需要判断文章模板路径
//        literatureService.checkContentTemplatePath(saveLiterature);

//        ArticleDetailVO articleDetailVO = contentService.convert(content);
//        ArticleDetailVO articleDetailVO = articleService.convert(article);


        htmlService.conventHtml(saveLiterature);
        htmlService.generateCollectionTree();

        return saveLiterature;
    }

    @GetMapping("/delAll")
    public BaseResponse delAll(){
         literatureService.deleteAll();
         return BaseResponse.ok("success");
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
    @GetMapping("/generateListHtml")
    public BaseResponse generateListHtml(HttpServletRequest request){
        int userId = AuthorizationUtil.getUserId(request);
        literatureService.generateListHtml(userId);
        return BaseResponse.ok("success!!");

    }


    @GetMapping("/generateHtml/{id}")
    public Content generateHtml(@PathVariable("id") Integer id){

//        TestStatic.test();
        Literature literature = literatureService.findById(id);
        // 需要判断文章模板路径
//        contentService.checkContentTemplatePath(content);

//        ArticleDetailVO articleDetailVO = contentService.convert(content);
//        ArticleDetailVO articleDetailVO = articleService.convert(article);
        LiteratureVo literatureVo1 = literatureService.convertToVo(literature);
        LiteratureVo literatureVo = literatureService.convertToTagVo(literatureVo1);
        htmlService.conventHtml(literatureVo);
        return literature;
    }

}
