package com.wangyang.interfaces.componentsdata;

import com.google.common.base.Joiner;
import com.wangyang.common.CmsConst;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.interfaces.IComponentsData;
import com.wangyang.pojo.dto.ArticlePageCondition;
import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.Components;
import com.wangyang.pojo.vo.ArticleVO;
import com.wangyang.repository.template.ComponentsRepository;
import com.wangyang.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.util.*;

@Component
public class ArticlePageData implements IComponentsData {

    @Autowired
    IArticleService articleService;

//    @Autowired
//    IComponentsService componentsService;

    @Autowired
    ComponentsRepository componentsRepository;

    @Override
    public String getDataName() {
        return CmsConst.ARTICLE_DATA_SORT_SIZE;
    }

    @Override
    public Map<String,Object> getData(Components components) {
        return  getModelPageSize(components,0,5,"DESC");
    }

    @Override
    public Boolean cacheDate(String pathArg) {
//        component_41,category_2,sort_visits,order_DESC,page_1,size_5
        String[] argsSplit = pathArg.split(",");

        List<String> argLists = Arrays.asList(argsSplit);
        int size=5;
        int page=0;
        int componentId=0;
        String order="DESC";
        Set<Integer> ids = new HashSet<>();
        Set<String> sortStr = new HashSet<>();
        for (String arg : argLists){
            if(arg.startsWith("size_")){
                size = Integer.parseInt(arg.replace("size_", ""));
            }else if(arg.startsWith("page_")){
                page = Integer.parseInt(arg.replace("page_", ""));
            }else  if(arg.startsWith("order_")){
                order = arg.replace("order_", "");

            }else if(arg.startsWith("sort_")){
                String sort_ = arg.replace("sort_", "");
                sortStr.add(sort_);
            }else if(arg.startsWith("component_")){
                componentId = Integer.parseInt(arg.replace("component_", ""));
            }else if(arg.startsWith("category_")){
                String categoryIds = arg.replace("category_", "");
                String[] idsSplit = categoryIds.split("\\|");
                for(String i : idsSplit){
                    ids.add(Integer.parseInt(i));
                }

            }

        }

//        String url = "component_"+componentId+",category_"+Joiner.on(",").join(ids)+",sort_"+Joiner.on(",").join(sortStr)+",order_"+order+",page_"+(page+1)+",size_"+size;
//        if(TemplateUtil.checkFileExist("html/components/"+componentId,url)){
//            return TemplateUtil.openFile("html/components/"+componentId,url);
//        }


        String url = "components/"+componentId+"/component_"+componentId+",category_"+
                Joiner.on("|").join(ids)+
                ",sort_"+Joiner.on(",").join(sortStr)+
                ",order_"+order+
                ",page_"+(page+ 1)+
                ",size_"+size+"-"+CmsConst.ARTICLE_DATA_SORT_SIZE;

        Components components = componentsRepository.findById(componentId).orElse(null);
        if(components==null){
            throw new ObjectException("对象不存在！");
        }
        Map<String,Object> map = new HashMap<>();
        ArticlePageCondition articlePageCondition = articleService.pagePublishBy(ids, sortStr, order, page, size);
        Page<Article> articles =articlePageCondition.getArticles();
        Page<ArticleVO> articleVOS = articleService.convertToPageVo(articles);
        if(articleVOS.getContent().isEmpty()){
            throw new ObjectException("没有数据！！");
        }
//                Map<String,Object> map = new HashMap<>();

        map.put("view",articleVOS);
        map.put("info",articlePageCondition);

        map.put("url",url);
//        map.put("showUrl","/articleList?sort="+orderSort); //likes,DESC
//        TemplateUtil.convertHtmlAndSave(category.getPath()+CMSUtils.getArticleRecommendPath(),category.getViewName(),map,template);
        Context context = new Context();
        context.setVariables(map);
        String html = TemplateUtil.getHtml(components.getTemplateValue(),context);
        String path = "html/components/"+componentId;
        String viewName = "component_"+componentId+",category_"+
                Joiner.on("|").join(ids)+
                ",sort_"+Joiner.on(",").join(sortStr)+
                ",order_"+order+
                ",page_"+(page)+
                ",size_"+size+"-"+CmsConst.ARTICLE_DATA_SORT_SIZE;

        TemplateUtil.saveFile(path,viewName,html);
//        return html;
//
//        String html = htmlService.articlePageCondition(componentId, ids, sortStr, order, page, size);
        return true;
    }
///home/wy/cms/html/components/41/component_41,category_2,sort_visits,order_DESC,page_2,size_5-@SizeSortArticle.html




    //    @GetMapping(value = "/component_{id},category_{ids},sort_{sort},order_{order},page_{page},size_{size}",produces={"text/html;charset=UTF-8;","application/json;"})
////    @Anonymous
////    @ResponseBody
//    public String articlePageCondition(@PathVariable("id") Integer componentId,
//                                       @PathVariable("ids") String categoryIds,
//                                       @PathVariable("sort") String sort,
//                                       @PathVariable("order") String order,
//                                       @PathVariable("page") Integer page,
//                                       @PathVariable("size") Integer size){
////        response.setContentType("");
//        Set<Integer> ids= new HashSet<>();
//        String[] idsSplit = categoryIds.split(",");
//        for(String i : idsSplit){
//            ids.add(Integer.parseInt(i));
//        }
//        String[] sortSplit = sort.split(",");
//        Set<String>  sortStr= new HashSet<>();
//        sortStr.addAll(Arrays.asList(sortSplit));
//
//        String html = htmlService.articlePageCondition(componentId, ids, sortStr, order, page, size);
////        TemplateUtil.saveFile(path,viewName,html);
////        Map<String,String> map = new HashMap<>();
////        map.put("html",html);
////        map.put("url",html);
//
//        return html;
//
//    }
//

    public Map<String ,Object> getModelPageSize(Components components, Integer page, Integer size, String order) {
        TemplateUtil.deleteFile("html/components/"+components.getId());
        Map<String,Object> map = new HashMap<>();
        String args = components.getDataArgs(); //getDataName().substring(CmsConst.ARTICLE_DATA_SORT_SIZE.length());
        if(args==null||"".equals(args)){
            throw new ObjectException("数据参数不能为空！！");
        }
        String[] argsArray = args.split(",");
        List<String> argLists = Arrays.asList(argsArray);
//        int size=5;

        Set<String> sortStr = new HashSet<>();
        for (String arg : argLists){
            if(arg.startsWith("size_")){
                size = Integer.parseInt(arg.replace("size_", ""));
            }else  if(arg.startsWith("order_")){
                order = arg.replace("order_", "");

            }else if(arg.startsWith("sort_")){
                String sort_ = arg.replace("sort_", "");
                sortStr.add(sort_);
            }
        }
//        String orderSort = sortStr.stream()
//                .collect(Collectors.joining(","))+","+direction.name();



        ArticlePageCondition articlePageCondition= articleService.pagePublishBy(components.getId(),sortStr,order,page, size);
        Page<Article> articles =articlePageCondition.getArticles();
        Page<ArticleVO> articleVOS = articleService.convertToPageVo(articles);
//                Map<String,Object> map = new HashMap<>();
        map.put("view",articleVOS);
        map.put("info",articlePageCondition);
//        map.put("showUrl","/articleList?sort="+orderSort); //likes,DESC
        map.put("name",components.getName());
        map.put("componentIds",components.getId());
        map.put("url","components/"+components.getId()+"/component_"+components.getId()+",category_"+
                Joiner.on("|").join(articlePageCondition.getIds())+
                ",sort_"+Joiner.on(",").join(articlePageCondition.getSortStr())+
                ",order_"+articlePageCondition.getOrder()+
                ",page_"+(articlePageCondition.getPage()+ 1)+
                ",size_"+articlePageCondition.getSize()+"-"+CmsConst.ARTICLE_DATA_SORT_SIZE);

        return map;
    }
}
