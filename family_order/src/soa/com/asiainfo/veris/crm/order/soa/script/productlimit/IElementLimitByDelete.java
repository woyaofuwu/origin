
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 校驗業務刪除一個元素時的接口
 * @author: xiaocl
 */
public interface IElementLimitByDelete
{
    void checkAllElementLimitDelete(IData databus, IDataset listTradeElement, IDataset listUserAllElement, boolean bIsPkgInsideElmentLimit, CheckProductData checkProductData) throws Exception;
}
