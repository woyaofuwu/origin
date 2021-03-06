
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 針對產品受理中變更的校驗接口
 * @author: xiaocl
 */
public interface IProductLimitByModifyModify
{
    abstract public void checkProductLimitByModify(IData databus, IDataset listUserAllProduct, IDataset listTradeProduct, CheckProductData checkProductData) throws Exception;
}
