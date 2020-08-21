/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.broadband.widenet.querywidenetusercomplaints;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: QueryWidenetUserComplaints.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-5-23 下午03:00:16 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-5-23 chengxf2 v1.0.0 修改原因
 */

public abstract class QueryWidenetUserComplaints extends CSBasePage
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-22 下午08:54:30 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-22 chengxf2 v1.0.0 修改原因
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_MODE", "07");
        IDataset result = CSViewCall.call(this, "CS.ProductInfoQrySVC.qryProductsByProductMode", data);
        this.setWidenetProductSet(result);
    }

    public abstract void setWidenetProductSet(IDataset widenetProductSet);

}
