package com.neuedu.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.neuedu.annotation.DateUtils;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.Product;
import com.neuedu.vo.ProductDetailVO;
import com.neuedu.vo.ProductListVO;
import com.neuedu.service.ICategoryService;
import com.neuedu.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    ICategoryService categoryService;
    @Override
    public ServerResponse saveOrUpdate(Product product) {

        //step1:参数非空校验
        if(product==null){
            return ServerResponse.createServerResponseBySucess("参数为空");
        }
        //step2:设置商品主图sub_images-->1.jpg 2.jpg 3.jpg
        String subImage=product.getSubImages();
        if(subImage!=null&&!subImage.equals("")){
            String[] subImageArr=subImage.split(",");
            if(subImageArr.length>0){
                //设置商品的主图
                product.setMainImage(subImageArr[0]);
            }
        }
        //step3:商品save or update
        if(product.getId()==null){
         //添加
            int count=productMapper.insert(product);
            if(count>0){
                return ServerResponse.createServerResponseBySucess("商品添加成功");
            }else{
                return ServerResponse.createServerResponseByFail("商品添加失败");
            }
        }else{
            //更新
            int count=productMapper.updateByPrimaryKey(product);
            if(count>0){
                return ServerResponse.createServerResponseBySucess("商品更新成功");
            }else{
                return ServerResponse.createServerResponseByFail("商品更新失败");
            }
        }
    }
@Override
public ServerResponse detail(Integer productId) {
    //step1:参数校验
    if(productId==null){
        return ServerResponse.createServerResponseByFail("参数不能为空");
    }
    //step2:查询商品
    Product product=productMapper.selectByPrimaryKey(productId);
    if(product==null){
        return ServerResponse.createServerResponseByFail("商品不存在");
    }
    //step3:product-->productDetailVO
    ProductDetailVO productDetailVO=assembleProductDetailVO(product);
    //step4:根据商品id查询详情
    return ServerResponse.createServerResponseBySucess(productDetailVO);

}

    @Override
    public ServerResponse detail_portal(Integer productId) {
                //step1:参数校验
        if(productId==null){
            return ServerResponse.createServerResponseByFail("参数不能为空");
        }
        //step2:查询商品
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createServerResponseByFail("商品不存在");
        }
        //step3:校验商品状态
        if(product.getStatus()!=Const.ProductStatusEnum.PRODUCT_ONLINE.getCode()){
            return ServerResponse.createServerResponseByFail("商品已经下架或删除");
        }

        //step4:根据商品id查询详情
        //ProductDetailVO productDetailVO=assembleProductDetailVO(product);
       return ServerResponse.createServerResponseBySucess(product);

    }

    @Override
    public ServerResponse list_portal(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy) {
       //step1:参数校验 categoryId和keyword不能同时为空
        if(categoryId==null&&(keyword==null||keyword.equals(""))){
            return ServerResponse.createServerResponseByFail("参数不能为空");
        }
        //step2:categoryId
        Set<Integer> integerSet= Sets.newHashSet();
        if(categoryId!=null){
            Category category=categoryMapper.selectByPrimaryKey(categoryId);
            if(category==null&&(keyword==null||keyword.equals(""))){
                //说明没有商品数据
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVO> productListVOS =Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVOS);
                return ServerResponse.createServerResponseBySucess(pageInfo);
            }
            ServerResponse serverResponse=categoryService.get_deep_category(categoryId);

            if(serverResponse.isSucess()){
                integerSet=(Set<Integer>)serverResponse.getData();
            }
        }
            //step3:keyword
            if(keyword!=null&&!keyword.equals("")){
                keyword="%"+keyword+"%";
            }
            if(orderBy.equals("")){
                PageHelper.startPage(pageNum,pageSize);
            }else{
                String[] orderByArr=orderBy.split("_");
                if(orderByArr.length>1){
                    PageHelper.startPage(pageNum,pageSize,orderByArr[0]+" "+orderByArr[1]);
                }else{
                    PageHelper.startPage(pageNum,pageSize);
                }
            }
            //step4:List<Product>-->List<ProductListVO>
            List<Product> productList=productMapper.searchProduct(integerSet,keyword);
            List<ProductListVO> productListVOS =Lists.newArrayList();
            if(productList!=null&&productList.size()>0){
                for(Product product:productList){
                    ProductListVO productDetailVO=assembleProductListVo(product);
                    productListVOS.add(productDetailVO);
                }
            }
            //step5:分页
            PageInfo pageInfo = new PageInfo(productList);
            pageInfo.setList(productListVOS);
            //step6:返回
            return ServerResponse.createServerResponseBySucess(pageInfo);

    }

    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        //step1:查询商品数据
        List<Product> productList=productMapper.selectAll();
        List<ProductListVO> productListVOS = Lists.newArrayList();
        if(productList!=null&&productList.size()>0){
            for(Product product:productList){
                ProductListVO productDetailVO=assembleProductListVo(product);
                productListVOS.add(productDetailVO);
            }
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVOS);
        return ServerResponse.createServerResponseBySucess(pageInfo);
    }

    @Override
    public ServerResponse topcategory(Integer sid) {

        return null;
    }

    private ProductDetailVO assembleProductDetailVO(Product product){
        ProductDetailVO productDetailVO=new ProductDetailVO();
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setCreateTime(DateUtils.dateToStr(product.getCreateTime()));
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setName(product.getName());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setId(product.getId());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setUpdateTime(DateUtils.dateToStr(product.getUpdateTime()));
        Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category!=null)
        {productDetailVO.setParentCategoryId(category.getParentId());}
        else{
            productDetailVO.setParentCategoryId(0);
        }
        return productDetailVO;
    }
    private ProductListVO assembleProductListVo(Product product){
        ProductListVO productListVO =new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setName(product.getName());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        productListVO.setSubtitle(product.getSubtitle());
        return productListVO;
    }
}
