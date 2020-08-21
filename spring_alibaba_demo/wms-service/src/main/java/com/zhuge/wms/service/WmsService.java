package com.zhuge.wms.service;

import org.springframework.stereotype.Service;

@Service
public class WmsService {

    public String delivery(Long userId, Long productId) {
        System.out.println("用户userId= "+ userId +"的商品productId=" + productId + "：生成发货单");
        return "success";
    }

}