
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 針對產品互轉的接口
 * @author: xiaocl
 */
public interface IProductTransLimit
{
    abstract void checkProductTransLimit(IData databus, IDataset listTradeProduct, CheckProductData checkProductData) throws Exception;
}
