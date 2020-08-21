
package com.asiainfo.veris.crm.order.web.person.bat.ocs;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: OcsUserImport.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: huanghui
 * @date: 2013-7-13 下午3:22:43 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-7-13 huanghui v1.0.0 修改原因
 */

public abstract class OcsBatchQuery extends PersonBasePage
{

    /**
     * @Function: queryOcsDealInfo
     * @Description: OCS导入结果查询
     * @param： IRequestCycle
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: huanghui@asiainfo-linkage.com
     * @date: 下午3:07:59 2013-10-20 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-10-20 huanghui v1.0.0
     */
    public void queryOcsDealInfo(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryOcsDealInfo", pageData, getPagination("taskNav"));
        setTaskInfos(output.getData());
        setBatchTaskListCount(output.getDataCount());
    }

    public abstract void setBatchTaskListCount(long batchTaskListCount);

    public abstract void setTaskInfo(IData taskInfo);

    public abstract void setTaskInfos(IDataset taskInfos);
}
