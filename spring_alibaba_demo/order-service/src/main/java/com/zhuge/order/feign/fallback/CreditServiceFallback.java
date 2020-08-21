package com.zhuge.order.feign.fallback;

import com.zhuge.order.feign.CreditService;
import org.springframework.stereotype.Component;

@Component
public class CreditServiceFallback implements CreditService {

    @Override
    public String addCredit(Long userId, Integer creditCount) {
        //TODO 记录降级日志，后续自动补偿
        System.out.println("调用加积分服务失败，记录日志降级处理：userId=" + userId + ",creditCount = "+ creditCount);
        return "error";
    }
}