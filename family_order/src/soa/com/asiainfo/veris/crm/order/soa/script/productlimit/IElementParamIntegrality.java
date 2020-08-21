
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 校驗元素的參數完整性接口
 * @author: xiaocl
 */
public interface IElementParamIntegrality
{
    abstract void checkElementParamIntegrality(IData databus, IDataset listTradeElement, IDataset listUserAllAttr, CheckProductData checkProductData) throws Exception;
}
