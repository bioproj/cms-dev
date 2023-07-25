package com.wangyang.service;

import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.Menu;
import com.wangyang.pojo.vo.MenuVo;
import com.wangyang.common.service.ICrudService;

import java.util.List;

public interface IMenuService extends ICrudService<Menu,Menu, MenuVo,Integer> {
    Menu add(Menu menu);

    Menu findById(int id);

    Menu update(int id, Menu updateMenu);

    void delete(int id);

    Menu removeCategoryToMenu(int id);

    List<MenuVo> listVo();

    Menu addCategoryToMenu(Category category);

    List<Menu> list();
}
