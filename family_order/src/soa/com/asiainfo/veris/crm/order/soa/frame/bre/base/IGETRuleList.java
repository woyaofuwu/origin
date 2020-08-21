
package com.asiainfo.veris.crm.order.soa.frame.bre.base;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage *
 * 
 * @ClassName: GetRuleList
 * @Description: 获取规则通用接口
 * @version: v1.0.0
 * @author: xiaocl
 */
public interface IGETRuleList
{
    abstract public IDataset getRuleList(IData databus, Object o) throws Exception;
}
