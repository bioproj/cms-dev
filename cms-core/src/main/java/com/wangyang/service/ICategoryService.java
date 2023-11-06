package com.wangyang.service;

import com.wangyang.pojo.dto.CategoryChild;
import com.wangyang.pojo.dto.CategoryDto;
import com.wangyang.pojo.entity.Category;
import com.wangyang.common.enums.Lang;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.params.CategoryQuery;
import com.wangyang.pojo.vo.CategoryDetailVO;
import com.wangyang.pojo.vo.CategoryVO;
import com.wangyang.service.base.IBaseCategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ICategoryService  extends IBaseCategoryService<Category,Category,CategoryVO> {
//    List<Category> listByIdsOrderComponent(Set<Integer> categoryIds);
//    List<CategoryVO> listByComponentsId(int componentsId);
    List<CategoryChild> listChildByComponentsId(int componentsId);



    Category save(Category category);

    /**
     * add category
     * @param category
     * @return
     */
    Category create(Category category,Set<Integer> tagIds,Integer userId);
    /**
     * add category
     * @param category
     * @return
     */
    Category update(Category category, Set<Integer> tagIds,Integer userId);

//    Page<CategoryDto> pageBy(String categoryEnName, int page, int size);

    Page<Category> pageBy(String categoryEnName, Pageable pageable);

    List<CategoryDto> listBy(String categoryEnName);



    List<Category> findAllById(Iterable<Integer> ids);

    /**
     * delete by Id
     * @param id
     */
    void deleteById(int id);



    /**
     * find category by id
     * @param id
     * @return
     */
    Category findById(int id);




    List<CategoryDto> listRecommend();

    Optional<Category> findOptionalById(int id);

    List<CategoryVO> listChildWithTree(String viewName);

    List<Category> list(CategoryQuery categoryQuery, Sort sort);

    List<CategoryDto> listAllDto();



    List<Category> listAll();

    /**
     * 不显示haveHtml=false的Category
     * 生成菜单树
     * @return
     */
    List<CategoryVO> listUserCategoryVo();

    /**
     * 显示haveHtml=false的Category
     * @return
     */
    List<CategoryVO> listAdminCategoryVo(Lang lang);

//    List<Category> list();

    Category recommendOrCancelHome(int id);

    Category haveHtml(int id);

    Category addOrRemoveToMenu(int id);


    CategoryDto covertToDto(Category category);

//    CategoryVO covertToVo(Category category);

    List<CategoryVO> listChildByViewName(String viewName);

    Category findByViewName(String viewName, Lang lang);


    void updateOrder(List<CategoryVO> categoryVOList);


    CategoryDetailVO covertToDetailVO(Category category);

    Category createCategoryLanguage(Integer id, Lang lang);

    Category createCategoryLanguage(Category category, Lang lang);



}
