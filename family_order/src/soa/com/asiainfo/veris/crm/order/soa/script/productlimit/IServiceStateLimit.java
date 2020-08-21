
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 針對服務狀態的校驗接口 .
 * @author: xiaocl
 */
public interface IServiceStateLimit
{

    void CheckServiceStateLimit(IData databus, IDataset listTradeSvcState, IDataset listUserAllSvcState, CheckProductData checkProductData) throws Exception;
}
