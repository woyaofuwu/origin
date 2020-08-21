/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.multioffer;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.bm.BmServiceClient;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: MultiOfferHandler.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-28 下午05:18:30 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-28 chengxf2 v1.0.0 修改原因
 */

public class MultiOfferHandler extends BizHttpHandler
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-28 下午05:19:17 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-28 chengxf2 v1.0.0 修改原因
     */
    public void getPageInfo() throws Exception
    {
        IData data = getData();
        String pageId = data.getString("PAGE_ID");
        IData pageInfo = BmServiceClient.getPageInfo(this, pageId);
        if (!pageInfo.isEmpty())
        {
            IDataset dataset = BmServiceClient.getPageDataSetList(this, pageId);
            pageInfo.put("DATASET", dataset);
        }
        setAjax(pageInfo);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-29 下午04:55:06 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-29 chengxf2 v1.0.0 修改原因
     */
    public void needSelProduct() throws Exception
    {
        IData data = getData();
        data.put("NEED_SEL_PROD", true);
        this.setAjax(data);
    }
}
