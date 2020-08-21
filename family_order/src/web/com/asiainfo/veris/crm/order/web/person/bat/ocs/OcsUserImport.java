
package com.asiainfo.veris.crm.order.web.person.bat.ocs;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: OcsUserImport.java
 * @Description: 批量号码OCS导入
 * @version: v1.0.0
 * @author: xiangyc
 * @date: 2013-7-13 下午3:22:43 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-7-13 xiangyc v1.0.0 修改原因
 */

public abstract class OcsUserImport extends PersonBasePage
{

    public void getBatchId(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IData result = new DataMap();
        IDataset results = CSViewCall.call(this, "CS.BatDealSVC.initBatchId", pageData);
        if (IDataUtil.isNotEmpty(results))
        {
            result = (IData) (results.get(0));
        }
        setBatchId(result);
    }

    public void importOcsData(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        CSViewCall.call(this, "CS.BatDealSVC.importOcsData", pageData);
    }

    public abstract void setBatchId(IData batchId);

}
