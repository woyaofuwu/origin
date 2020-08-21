package com.zhuge.wms.controller;

import com.zhuge.wms.service.WmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wms")
public class WmsController {

    @Autowired
    private WmsService wmsService;

    @PostMapping(value = "/delivery/{userId}/{productId}")
    public String delivery(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId) {
        return wmsService.delivery(userId, productId);
    }
}
