/**
 * 
 */

package com.asiainfo.veris.crm.order.web.frame.csview.common.query.bm;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BmServiceFactory.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-19 上午11:36:15 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-19 chengxf2 v1.0.0 修改原因
 */

public final class BmServiceFactory
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-19 上午11:38:58 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-19 chengxf2 v1.0.0 修改原因
     */
    public static IData getFrameInfo(IBizCommon bizCommon, String frameId) throws Exception
    {
        IData data = new DataMap();
        data.put("FRAME_ID", frameId);
        IDataset result = CSViewCall.call(bizCommon, "CS.BmFrameInfoQrySVC.getFrameInfo", data);
        return result.isEmpty() ? new DataMap() : result.getData(0);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-19 上午11:46:02 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-19 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getPageDataSetList(IBizCommon bizCommon, String pageId) throws Exception
    {
        IData data = new DataMap();
        data.put("PAGE_ID", pageId);
        return CSViewCall.call(bizCommon, "CS.BmFrameInfoQrySVC.getPageDataSetList", data);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-19 上午11:49:57 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-19 chengxf2 v1.0.0 修改原因
     */
    public static IData getPageInfo(IBizCommon bizCommon, String pageId) throws Exception
    {
        IData data = new DataMap();
        data.put("PAGE_ID", pageId);
        IDataset result = CSViewCall.call(bizCommon, "CS.BmFrameInfoQrySVC.getPageInfo", data);
        return result.isEmpty() ? new DataMap() : result.getData(0);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-19 上午11:50:38 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-19 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getPageRuleSetList(IBizCommon bizCommon, String ruleSetId) throws Exception
    {
        IData data = new DataMap();
        data.put("RULESET_ID", ruleSetId);
        return CSViewCall.call(bizCommon, "CS.BmFrameInfoQrySVC.getPageRuleSetList", data);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-19 上午11:45:07 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-19 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getPageSetPageList(IBizCommon bizCommon, String pageSetId) throws Exception
    {
        IData data = new DataMap();
        data.put("PAGESET_ID", pageSetId);
        return CSViewCall.call(bizCommon, "CS.BmFrameInfoQrySVC.getPageSetPageList", data);
    }
}
