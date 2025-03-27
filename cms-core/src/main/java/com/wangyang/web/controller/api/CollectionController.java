package com.wangyang.web.controller.api;

import com.wangyang.common.BaseResponse;
import com.wangyang.pojo.entity.Collection;
import com.wangyang.pojo.entity.Task;
import com.wangyang.pojo.vo.CollectionVO;
import com.wangyang.service.ICollectionService;
import com.wangyang.service.IZoteroService;
import com.wangyang.util.AuthorizationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/collection")
public class CollectionController {

    @Autowired
    ICollectionService collectionService;

    @Autowired
    IZoteroService zoteroService;

    @GetMapping
    public List<CollectionVO> listTree(){
      return collectionService.listTree();
    }


    @GetMapping("/list")
    public List<Collection> list(){
        return collectionService.listAll();
    }
    @GetMapping("/delAll")
    public BaseResponse delAll(){
        collectionService.deleteAll();
        return BaseResponse.ok("success");
    }

    @GetMapping("/import")
    public Task importData(HttpServletRequest request)  {
        int userId = AuthorizationUtil.getUserId(request);
        Task task = zoteroService.importCollection(userId);
        return task;
    }

    @GetMapping("/generateListHtml")
    public BaseResponse generateListHtml(HttpServletRequest request){
        collectionService.generateListHtml();
        return BaseResponse.ok("success!!");

    }
}
