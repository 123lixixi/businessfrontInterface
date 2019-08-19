package com.neuedu.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.neuedu.annotation.DateUtils;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.*;
import com.neuedu.pojo.*;
import com.neuedu.service.IOrderService;
import com.neuedu.util.BigDecimalUtils;
import com.neuedu.vo.OrderItemVO;
import com.neuedu.vo.OrderVO;
import com.neuedu.vo.ShippingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    ShippingMapper shippingMapper;
    @Override
    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        //step1:参数非空校验
        if(shippingId==null){
            return ServerResponse.createServerResponseByFail("参数不能为空");
        }
        //step2:根据userId查询购物车中已选中的商品-->List<Cart>
        List<Cart> cartList=cartMapper.findCartByUserIdAndChecked(userId);
        //step3:List<Cart>-->List<OrderItem>
        ServerResponse serverResponse=getCartOrderItem(userId,cartList);
        if(!serverResponse.isSucess()){
            return serverResponse;
        }
        //step4:创建订单order并将其保存到数据库
        //计算订单的总价
        BigDecimal orderTotalPrice=new BigDecimal(0);
        List<OrderItem> orderItemList=(List<OrderItem>) serverResponse.getData();
        if(orderItemList==null||orderItemList.size()==0){
           return ServerResponse.createServerResponseByFail("购物车为空");
        }
        orderTotalPrice=getOrderPrice(orderItemList);
        Order order=createOrder(userId,shippingId,orderTotalPrice);
        if(order==null){
            return ServerResponse.createServerResponseByFail("创建订单失败");
        }
        //step5:将List<OrderItem>保存到数据库
           for(OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
           }
           //批量插入
        orderItemMapper.insertBatch(orderItemList);
        //step6:扣库存
        reduceProductStock(orderItemList);
        //step7:购物车中清空已下单的商品
        cleanCart(cartList);
        //step8:返回，OrderVO
         OrderVO orderVO=assembleOrderVo(order,orderItemList,shippingId);

        return ServerResponse.createServerResponseBySucess(orderVO);
    }

    @Override
    public ServerResponse list(Integer userId, Integer pageNum, Integer pageSize) {
        //step1:获取订单列表
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList=orderMapper.selectAllByUserId(userId);
        if(orderList!=null) {
            List<OrderVO> orderVOList=assembleOrderVOList(orderList,userId);
            PageInfo pageInfo = new PageInfo(orderList);
            pageInfo.setList(orderVOList);
            return ServerResponse.createServerResponseBySucess(pageInfo);
        }
        return ServerResponse.createServerResponseByFail("未查询到订单信息");
    }

    @Override
    public ServerResponse detail(Integer userId, Long orderNo) {
        if(orderNo==null){
            return ServerResponse.createServerResponseByFail("参数不能为空");
        }
        Order order=orderMapper.selectByOrderNo(orderNo,userId);
        if(order==null){
            return ServerResponse.createServerResponseByFail("未查询到订单信息");
        }
        List<OrderItem> orderItemList=orderItemMapper.selectAllByOrderNo(orderNo,userId);
        OrderVO orderVO=assembleOrderVo(order,orderItemList,order.getShippingId());
        return ServerResponse.createServerResponseBySucess(orderVO);
    }

    @Override
    public ServerResponse cancel(Integer userId, Long orderNo) {
        Order order=orderMapper.selectByOrderNo(orderNo,userId);
        if(orderNo==null){
            return ServerResponse.createServerResponseByFail("参数不能为空");
        }
        if(order==null){
            return ServerResponse.createServerResponseByFail("该用户没有此订单");
        }
        if(order.getStatus()==Const.OrderStatusEnum.ORDER_UN_PAY.getCode()){
            order.setStatus(Const.OrderStatusEnum.ORDER_CANCELED.getCode());
            orderMapper.updateByPrimaryKey(order);
            return ServerResponse.createServerResponseBySucess("订单取消成功");
        }
        return ServerResponse.createServerResponseByFail("订单不可取消");
    }

    private List<OrderVO> assembleOrderVOList(List<Order> orderList,Integer userId){
        List<OrderVO> orderVOList=Lists.newArrayList();
        for(Order order:orderList){
        List<OrderItem> orderItemList=orderItemMapper.selectAllByOrderNo(order.getOrderNo(),userId);
        OrderVO orderVO=assembleOrderVo(order,orderItemList,order.getShippingId());
        orderVOList.add(orderVO);
        }
        return orderVOList;
}
    private OrderVO assembleOrderVo(Order order, List<OrderItem> orderItemList, Integer shippingId){
        OrderVO orderVO=new OrderVO();
        List<OrderItemVO> orderItemVOList=Lists.newArrayList();
        for(OrderItem orderItem:orderItemList){
            OrderItemVO orderItemVO=assembleOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setImageHost("/uploadpic");
        Shipping shipping=shippingMapper.selectByPrimaryKey(shippingId);
        if(shipping!=null){
            orderVO.setShippingId(shippingId);
            ShippingVO shippingVO=assembleShippingVO(shipping);
            orderVO.setShippingVO(shippingVO);
            orderVO.setReceiverName(shipping.getReceiverName());
        }
        orderVO.setStatus(order.getStatus());
        Const.OrderStatusEnum orderStatusEnum=Const.OrderStatusEnum.codeOf(order.getStatus());
        if(orderStatusEnum!=null){
            orderVO.setStatusDesc(orderStatusEnum.getDesc());
        }
        orderVO.setPostage(0);
        orderVO.setPayment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
        Const.PaymentEnum paymentEnum=Const.PaymentEnum.codeOf(order.getPaymentType());
        if(paymentEnum!=null){
            orderVO.setPaymentTypeDesc(paymentEnum.getDesc());

        }
        orderVO.setOrderNo(order.getOrderNo());
        return orderVO;
    }
    private ShippingVO assembleShippingVO(Shipping shipping){
        ShippingVO shippingVO=new ShippingVO();
        if(shipping!=null){
            shippingVO.setReceiverAddress(shipping.getReceiverAddress());
            shippingVO.setReceiverCity(shipping.getReceiverCity());
            shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
            shippingVO.setReceiverMobile(shipping.getReceiverMobile());
            shippingVO.setReceiverName(shipping.getReceiverName());
            shippingVO.setReceiverPhone(shipping.getReceiverPhone());
            shippingVO.setReceiverProvince(shipping.getReceiverProvince());
            shippingVO.setReceiverZip(shipping.getReceiverZip());
        }
        return shippingVO;
    }
    private OrderItemVO assembleOrderItemVO(OrderItem orderItem){
        OrderItemVO orderItemVO=new OrderItemVO();
        if(orderItem!=null){
            orderItemVO.setQuantity(orderItem.getQuantity());
            orderItemVO.setCreateTime(DateUtils.dateToStr(orderItem.getCreateTime()));
            orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVO.setOrderNo(orderItem.getOrderNo());
            orderItemVO.setProductId(orderItem.getProductId());
            orderItemVO.setProductImage(orderItem.getProductImage());
            orderItemVO.setProductName(orderItem.getProductName());
            orderItemVO.setTotalPrice(orderItem.getTotalPrice());
        }
        return orderItemVO;
    }
    /**
     * 清空购物车中已选择的商品
     */
    private void cleanCart(List<Cart> cartList){
        if(cartList!=null&&cartList.size()>0){
            cartMapper.batchDelete(cartList);
        }

    }
    /**
     *扣库存
     */
    private void reduceProductStock(List<OrderItem> orderItemList){
        if(orderItemList!=null&&orderItemList.size()>0){
            for(OrderItem orderItem:orderItemList){
                Integer productId=orderItem.getProductId();
                Integer quantity=orderItem.getQuantity();
                Product product=productMapper.selectByPrimaryKey(productId);
                product.setStock(product.getStock()-quantity);
                productMapper.updateByPrimaryKey(product);
            }
        }
    }
    /**
     * 计算订单的总价
     * */
    private BigDecimal getOrderPrice(List<OrderItem> orderItemList){
        BigDecimal bigDecimal=new BigDecimal(0);
        for(OrderItem orderItem:orderItemList){
            bigDecimal=BigDecimalUtils.add(bigDecimal.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return bigDecimal;
    }
    /**
     * 创建订单
     * @return
     */
    private Order createOrder(Integer userId,Integer shippingId,BigDecimal orderTotalPrice){
        Order order=new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setStatus(Const.OrderStatusEnum.ORDER_UN_PAY.getCode());
        //订单金额
        order.setPayment(orderTotalPrice);
        order.setPostage(0);
        order.setPaymentType(Const.PaymentEnum.ONLINE.getCode() );
        //保存订单
        int count=orderMapper.insert(order);
        if(count>0){
            return order;
        }
        return null;

    }
    /**
     * 生成订单编号
     * */
    private Long generateOrderNo(){
        return System.currentTimeMillis()+new Random().nextInt(100);
    }
    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){
        if(cartList==null||cartList.size()==0){
            return ServerResponse.createServerResponseByFail("购物车空");
        }
        List<OrderItem> orderItemList= Lists.newArrayList();
        for(Cart cart:cartList){
            OrderItem orderItem=new OrderItem();
            orderItem.setUserId(userId);
            Product product=productMapper.selectByPrimaryKey(cart.getProductId());
            if(product==null){
                return ServerResponse.createServerResponseByFail("id为"+cart.getProductId()+"的商品不存在");
            }
            if(product.getStatus()!= Const.ProductStatusEnum.PRODUCT_ONLINE.getCode()){
                return ServerResponse.createServerResponseByFail("id为"+product.getId()+"的商品已经下架");
            }
            if(product.getStock()<cart.getQuantity()){
                return ServerResponse.createServerResponseByFail("id为"+product.getId()+"的商品库存不足");
            }
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductId(cart.getProductId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createServerResponseBySucess(orderItemList);
    }
}
