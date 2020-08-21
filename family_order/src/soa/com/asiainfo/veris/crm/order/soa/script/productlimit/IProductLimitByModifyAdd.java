
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 新增產品時校驗接口
 * @author: xiaocl
 */
public interface IProductLimitByModifyAdd
{

    abstract public void checkProductLimitByAdd(IData databus, IDataset listUserAllProduct, IDataset listTradeProduct, CheckProductData checkProductData) throws Exception;
}
