
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import com.ailk.common.data.IData;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 初始化参数接口，包括往后的推荐模块涉及的参数
 * @author: xiaocl
 */
public interface IinitArgments
{
    abstract void initArgments(IData databus, CheckProductData checkProductData) throws Exception;
}
