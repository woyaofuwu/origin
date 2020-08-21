/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.multioffer;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.bm.BmServiceClient;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: MultiOffer.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-14 下午04:29:04 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-14 chengxf2 v1.0.0 修改原因
 */

public abstract class MultiOffer extends PersonBasePage
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-28 上午09:14:44 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-28 chengxf2 v1.0.0 修改原因
     */
    public void getPageInfo(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        String pageId = param.getString("PAGE_ID");
        IData pageInfo = BmServiceClient.getPageInfo(this, pageId);
        if (!pageInfo.isEmpty())
        {
            IDataset dataset = BmServiceClient.getPageDataSetList(this, pageId);
            pageInfo.put("DATASET", dataset);
        }
        setAjax(pageInfo);
    }
}
