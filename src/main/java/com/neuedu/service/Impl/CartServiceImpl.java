package com.neuedu.service.Impl;

import com.google.common.collect.Lists;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CartMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.pojo.Cart;
import com.neuedu.pojo.Product;
import com.neuedu.service.ICartService;
import com.neuedu.util.BigDecimalUtils;
import com.neuedu.vo.CartProductVO;
import com.neuedu.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
@Service
public class CartServiceImpl implements ICartService{
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Override
    public ServerResponse add(Integer userId,Integer productId, Integer count) {
        //step1:参数非空校验
         if(productId==null||count==null){
             return ServerResponse.createServerResponseByFail("参数不能为空");
         }
        //step2:根据productId和userId查询购物信息
        Cart cart=cartMapper.selectCartByUseridAndProduct(userId,productId);
         if(cart==null){
             //添加
             Cart cart1=new Cart();
             cart1.setUserId(userId);
             cart1.setProductId(productId);
             cart1.setQuantity(count);
             cart1.setChecked(Const.CartCheckedEnum.PRODUCT_CHECKED.getCode());
             cartMapper.insert(cart1);
         }else{
            return ServerResponse.createServerResponseByFail("商品已存在");

         }
         CartVO cartVO=getCartVOLimit(userId);
        return ServerResponse.createServerResponseBySucess(cartVO);
    }

    @Override
    public ServerResponse list(Integer userId) {
         CartVO cartVO=getCartVOLimit(userId);
        return ServerResponse.createServerResponseBySucess(cartVO);
    }

    @Override
    public ServerResponse update(Integer userId, Integer productId, Integer count) {
        //step1:参数判定
        if(productId==null||count==null){
            return ServerResponse.createServerResponseByFail("参数不能为空");
        }
        //step2:查询购物车中商品
        Cart cart=cartMapper.selectCartByUseridAndProduct(userId,productId);
        if(cart!=null){
            // step3:更新数量
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKey(cart);
        }
        //step4:返回cartVO
        return ServerResponse.createServerResponseBySucess(getCartVOLimit(userId));
    }

    @Override
    public ServerResponse delete_product(Integer userId, String productIds) {
        //step1:参数非空校验
        if(productIds==null||productIds.equals("")){
            return ServerResponse.createServerResponseByFail("参数不能为空");
        }
        //step2:productIds-->List<Integer>
        List<Integer> productIdList=Lists.newArrayList();
        String[] productIdsArr=productIds.split(",");
        if(productIdsArr!=null&&productIdsArr.length>0){
            for(String productIdstr:productIdsArr){
                Integer productId=Integer.parseInt(productIdstr);
                productIdList.add(productId);
            }
        }
        //step3:调用dao
        cartMapper.deleteByUseridAndProductIds(userId,productIdList);
        //step4:返回结果
        return ServerResponse.createServerResponseBySucess(getCartVOLimit(userId)) ;

    }

    @Override
    public ServerResponse select(Integer userId, Integer productId,Integer check) {
        //step1:dao接口
        cartMapper.selectOrUnselectProduct(userId,productId,check);
        //step2:返回结果
        return ServerResponse.createServerResponseBySucess(getCartVOLimit(userId));
    }

    @Override
    public ServerResponse get_cart_product_count(Integer userId) {
        //step1:调用dao
       int cart=cartMapper.get_cart_product_count(userId);
        //step2:返回结果
        return ServerResponse.createServerResponseBySucess(cart);
    }


    private CartVO getCartVOLimit(Integer userId){
        CartVO cartVo=new CartVO();
        //step1:根据userId查询购物信息-->List<Cart>
         List<Cart> cartList=cartMapper.selectCartByUserid(userId);
        //step2:List<Cart>-->List<CartProductVo>
        List<CartProductVO> cartProductVOList= Lists.newArrayList();
        //购物车总价格
        BigDecimal carttotalprice=new BigDecimal("0");
        if(cartList!=null&&cartList.size()>0){
            for(Cart cart:cartList){
               CartProductVO cartProductVO=new CartProductVO();
               cartProductVO.setId(cart.getId());
               cartProductVO.setQuantity(cart.getQuantity());
               cartProductVO.setUserId(cart.getUserId());
               cartProductVO.setProductChecked(cart.getChecked());
               //查询商品
                Product product=productMapper.selectByPrimaryKey(cart.getProductId());
               if(product!=null){
                   cartProductVO.setProductId(cart.getProductId());
                   cartProductVO.setProductMainImage(product.getMainImage());
                   cartProductVO.setProductName(product.getName());
                   cartProductVO.setProductPrice(product.getPrice());
                   cartProductVO.setProductStatus(product.getStatus());
                   cartProductVO.setProductStock(product.getStock());
                   cartProductVO.setProductSubtitle(product.getSubtitle());
                   int stock=product.getStock();
                   int limitProductCount=0;
                   if(stock>=cart.getQuantity()){
                       limitProductCount=cart.getQuantity();
                       cartProductVO.setLimitQuantity("LIMIT_NUM_SUCCESS");
                   }else{
                       //商品库存不足
                       limitProductCount=stock;
                       //更新购物车中商品的数量
                       cart.setQuantity(stock);
                       cartMapper.updateByPrimaryKey(cart);
                       cartProductVO.setLimitQuantity("LIMIT_NUM_FAIL");
                   }
                  cartProductVO.setQuantity(limitProductCount);
                  cartProductVO.setProductTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),Double.valueOf(cartProductVO.getQuantity())));

               }
               if(cartProductVO.getProductChecked()==Const.CartCheckedEnum.PRODUCT_CHECKED.getCode()){
                   carttotalprice=BigDecimalUtils.add(carttotalprice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());

               }
                cartProductVOList.add(cartProductVO);

            }
        }
       cartVo.setCartProductVOList(cartProductVOList);
        //step3:计算总价格
        cartVo.setCarttotalprice(carttotalprice);
        //step4:判断购物车是否全选
           int count=cartMapper.isCheckedAll(userId);
           if(count>0){
               cartVo.setIsallchecked(false);
           }else{
               cartVo.setIsallchecked(true);
           }
        //step5:返回结果
        return cartVo;
    }
}
