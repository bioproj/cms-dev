package com.wangyang.web.controller.api;

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
@Deprecated
public class CollectionController {

    @Autowired
    ICollectionService collectionService;

    @Autowired
    IZoteroService zoteroService;

    @GetMapping
    public List<CollectionVO> list(){
      return collectionService.listTree();
    }


    @GetMapping("/import")
    public Task importData(HttpServletRequest request)  {
        int userId = AuthorizationUtil.getUserId(request);
        Task task = zoteroService.importCollection(userId);
        return task;
    }

}
