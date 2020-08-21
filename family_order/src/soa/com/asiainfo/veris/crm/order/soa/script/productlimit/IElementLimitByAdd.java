
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 新增元素的時候的校驗接口
 * @author: xiaocl
 */
public interface IElementLimitByAdd
{
    void checkAllElementLimitAdd(IData databus, IDataset listTradeElement, IDataset listUserAllElement, boolean bIsPkgInsideElmentLimit, CheckProductData checkProductData) throws Exception;
}
