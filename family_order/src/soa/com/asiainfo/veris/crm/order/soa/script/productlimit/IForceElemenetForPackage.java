
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 校驗包下必選元素接口
 * @author: xiaocl
 */
public interface IForceElemenetForPackage
{
    abstract void checkForceElementForPackage(IData databus, String strTypeCode, IDataset listUserAllPackage, IDataset listTradePackage, IDataset listUserAllElement, CheckProductData checkProductData) throws Exception;
}
