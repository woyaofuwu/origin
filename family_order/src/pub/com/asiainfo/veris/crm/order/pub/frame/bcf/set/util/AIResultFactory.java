/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.util;

import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: AIResultFactory.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:33:52 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class AIResultFactory
{

    public static AIResult getInstance(IDataset dataList, BizPage bizPage) throws Exception
    {
        return new DcResult(dataList, bizPage);
    }
}
