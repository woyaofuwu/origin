/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BmFrameInfoQrySVC.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-14 上午11:31:04 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-14 chengxf2 v1.0.0 修改原因
 */

public class BmFrameInfoQrySVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-14 上午11:32:47 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-14 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getFrameInfo(IData inData) throws Exception
    {
        String frameId = inData.getString("FRAME_ID");
        return BmFrameInfoQry.getFrameInfo(frameId);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-14 上午11:35:16 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-14 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getPageDataSetList(IData inData) throws Exception
    {
        String pageId = inData.getString("PAGE_ID");
        return BmFrameInfoQry.getPageDataSetList(pageId);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-14 上午11:34:40 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-14 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getPageInfo(IData inData) throws Exception
    {
        String pageId = inData.getString("PAGE_ID");
        return BmFrameInfoQry.getPageInfo(pageId);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-14 上午11:35:55 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-14 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getPageRuleSetList(IData inData) throws Exception
    {
        String ruleSetId = inData.getString("RULESET_ID");
        return BmFrameInfoQry.getPageRuleSetList(ruleSetId);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-14 上午11:33:52 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-14 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getPageSetPageList(IData inData) throws Exception
    {
        String pageSetId = inData.getString("PAGESET_ID");
        return BmFrameInfoQry.getPageSetPageList(pageSetId);
    }
}
