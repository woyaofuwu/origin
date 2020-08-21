
package com.asiainfo.veris.crm.order.web.person.changesvcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: GuaranteeOpen
 * @Description: 担保开机
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2013-03-20 下午04:35:35
 */
public abstract class GuaranteeOpen extends ChangeSvcState
{
    /**
     * 查询担保号码/以及担保相关信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryGuaranteeSerialNumber(IRequestCycle cycle) throws Exception
    {
        IData pgData = getData();
        IDataset guaranteeInfos = CSViewCall.call(this, "SS.ChangeSvcStateSVC.queryGuaranteeInfo", pgData);
        setBusiInfo(guaranteeInfos.getData(0));
    }

    public abstract void setBusiInfo(IData data);
}
