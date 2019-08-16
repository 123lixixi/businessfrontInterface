package com.neuedu.service.Impl;

import com.google.common.collect.Sets;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.pojo.Category;
import com.neuedu.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements ICategoryService{
   @Autowired
    CategoryMapper categoryMapper;
    @Override
    public ServerResponse get_category(Integer integer) {
        //step1:非空校验
        if(integer==null){
            return ServerResponse.createServerResponseByFail("参数不能为空");
        }
        //step2:根据categoryid查询类别
        Category category=categoryMapper.selectByPrimaryKey(integer);
        if(category==null){
            return ServerResponse.createServerResponseByFail("查询的类别不存在");
        }
        //step3:查询子类别
        List<Category> categoryList=categoryMapper.findChildCategory(integer);
        //step4:返回结果

        return ServerResponse.createServerResponseBySucess(categoryList);
    }

    @Override
    public ServerResponse get_deep_category(Integer categoryId) {
        //step1:参数非空校验
        if(categoryId==null){
            return ServerResponse.createServerResponseByFail("类别不存在");
        }
        // step2:查询
        Set<Category> categorySet= Sets.newHashSet();
        findAllChildCategory(categorySet,categoryId);
        Set<Integer> integerSet=Sets.newHashSet();
        Iterator<Category> categoryIterator=categorySet.iterator();
        while(categoryIterator.hasNext()){
            Category category=categoryIterator.next();
            integerSet.add(category.getId());
        }

        return ServerResponse.createServerResponseBySucess(integerSet);
    }
    private Set<Category> findAllChildCategory(Set<Category> categories,Integer categoryId){
      Category category=categoryMapper.selectByPrimaryKey(categoryId);
      if(category!=null){
         categories.add(category);
      }
      //查找categoryId下的子节点（平级）
        List<Category> categoryList=categoryMapper.findChildCategory(categoryId);
        if(categoryList!=null&categoryList.size()>0){
            for(Category  category1:categoryList){
                findAllChildCategory(categories,category1.getId());
            }
        }
      return categories;
    }
}