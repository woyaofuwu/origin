
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 校驗產品必選包接口
 * @author: xiaocl
 */
public interface IForcePackageForProduct
{
    abstract void checkForcePackageForProduct(IData databus, IDataset listUserAllProduct, IDataset listUserAllPackage, IDataset listTradePackage, CheckProductData checkProductData) throws Exception;
}
