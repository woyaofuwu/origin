package com.zhuge.order.feign;

import com.zhuge.order.feign.fallback.CreditServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "credit-service", fallback = CreditServiceFallback.class)
public interface CreditService {

    /**
     * http://credit-service/credit/add/{userId}/{creditCount}
     *
     * @param userId
     * @param creditCount
     * @return
     */
    @PostMapping(value = "/credit/add/{userId}/{creditCount}")
    String addCredit(@PathVariable("userId") Long userId,
                     @PathVariable("creditCount") Integer creditCount);

}