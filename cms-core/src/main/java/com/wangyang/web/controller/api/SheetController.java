package com.wangyang.web.controller.api;

import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.enums.Lang;
import com.wangyang.service.IHtmlService;
import com.wangyang.service.ISheetService;
import com.wangyang.pojo.entity.Sheet;
import com.wangyang.pojo.enums.ArticleStatus;
import com.wangyang.pojo.vo.SheetVo;
import com.wangyang.pojo.params.SheetParam;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.util.AuthorizationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/sheet")
public class SheetController {

    @Autowired
    ISheetService sheetService;

    @Autowired
    IHtmlService htmlService;

    @PostMapping
    public Sheet add(@RequestBody SheetParam sheetParam, HttpServletRequest request){
        Sheet sheet = new Sheet();

        BeanUtils.copyProperties(sheetParam,sheet);
        int userId = AuthorizationUtil.getUserId(request);
        sheet.setUserId(userId);
        Sheet saveSheet = sheetService.addOrUpdate(sheet);
        htmlService.convertArticleListBy(saveSheet);
        return saveSheet;
    }

    @PostMapping("/save")
    public Sheet save(@RequestBody SheetParam sheetParam, HttpServletRequest request){
        Sheet sheet = new Sheet();
        BeanUtils.copyProperties(sheetParam,sheet);
        int userId = AuthorizationUtil.getUserId(request);
        sheet.setUserId(userId);
        Sheet saveSheet = sheetService.save(sheet);
//        htmlService.convertArticleListBy(saveSheet);
        return saveSheet;
    }

    @PostMapping("/save/{id}")
    public Sheet updateArticle(@PathVariable("id") Integer id, @Valid @RequestBody SheetParam sheetParam, HttpServletRequest request){
        Sheet sheet= sheetService.findById(id);
//        if(sheet.getOriginalContent().equals(sheetParam.getOriginalContent())sheetParam.getJsContent().equals()){
//            return sheet;
//        }
        BeanUtils.copyProperties(sheetParam,sheet,getNullPropertyNames(sheetParam));
        int userId = AuthorizationUtil.getUserId(request);
        sheet.setUserId(userId);
//        Boolean haveHtml = Optional.ofNullable(sheetParam.getHaveHtml()).orElse(false);
//        if(haveHtml){
//            article.setStatus(ArticleStatus.MODIFY);
//        }else {
//            article.setStatus(ArticleStatus.DRAFT);
//        }
        sheet.setStatus(ArticleStatus.MODIFY);
        return  sheetService.save(sheet);
    }
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
    @PostMapping("/update/{id}")
    public Sheet update(@PathVariable("id") int id,@RequestBody SheetParam sheetParam, HttpServletRequest request){
        Sheet sheet = findById(id);
        BeanUtils.copyProperties(sheetParam,sheet,getNullPropertyNames(sheetParam));
        int userId = AuthorizationUtil.getUserId(request);
        sheet.setUserId(userId);
        Sheet updateSheet = sheetService.addOrUpdate(sheet);
        htmlService.convertArticleListBy(updateSheet);
        return updateSheet;
    }

    @GetMapping
    public Page<SheetVo> list(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable,Sheet sheet){
        Page<Sheet> sheetPage = sheetService.pageBy(pageable,sheet);
        return sheetService.conventTo(sheetPage);
    }
    @GetMapping("/find/{id}")
    public Sheet findById(@PathVariable("id") Integer id){
        return sheetService.findById(id);
    }

    @GetMapping("/generate/{id}")
    public Sheet generate(@PathVariable("id") Integer id){
        Sheet sheet = sheetService.findById(id);
        sheet.setPdfPath(null);
        sheet = sheetService.save(sheet);
        htmlService.convertArticleListBy(sheet);
        return sheet;
    }
    @RequestMapping("/delete/{id}")
    public Sheet deleteById(@PathVariable("id") Integer id){
        Sheet sheet = sheetService.deleteById(id);
        TemplateUtil.deleteTemplateHtml(sheet.getViewName(),sheet.getPath());
        return sheet;
    }

    @GetMapping("/addOrRemoveToMenu/{id}")
    public Sheet addOrRemoveToMenu(@PathVariable("id") int id){
        Sheet sheet = sheetService.addOrRemoveToMenu(id);
//        htmlService.generateMenuListHtml();
        return sheet;
    }


    @GetMapping("/createSheetLanguage/{id}")
    public Sheet createSheetLanguage(@PathVariable("id") Integer id, @RequestParam(defaultValue = "EN") Lang lang){


        Sheet sheet = sheetService.findById(id);
        if(sheet.getLang()==null){
            sheet.setLang(Lang.ZH);
            sheetService.save(sheet);
        }
        if(sheet.getLang().equals(lang)){
            throw new ObjectException(sheet.getTitle()+"该文章已经是"+lang.getSuffix()+"的了！！！");
        }

        Sheet langSheet = sheetService.findByLang(sheet.getId(), lang);

        if(langSheet!=null){
            throw new ObjectException(langSheet.getTitle()+"已经创建了英文分类！！！");
        }

        Sheet newSheet = new Sheet();
        BeanUtils.copyProperties(sheet,newSheet,"id" );

        newSheet.setLangSource(sheet.getId());
        newSheet.setId(null);
        newSheet.setLang(lang);
        newSheet.setViewName(lang.getSuffix()+sheet.getViewName());
        newSheet.setTitle(lang.getSuffix()+sheet.getTitle());
        newSheet.setPath(sheet.getPath().replace("html",lang.getSuffix()));
        Sheet save = sheetService.save(newSheet);
        return save;

    }


//    @GetMapping("/findListByChannelId/{id}")
//    public List<SheetDto> findListByChannelId(@PathVariable("id") Integer id){
//        return sheetService.findListByChannelId(id);
//    }
}
