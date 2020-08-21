/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BmFrameInfoQry.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-13 下午02:44:01 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
 */

public final class BmFrameInfoQry
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-13 下午02:47:31 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getFrameInfo(String frameId) throws Exception
    {
        IData param = new DataMap();
        param.put("FRAME_ID", frameId);
        return Dao.qryByCode("TD_B_FRAME", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-13 下午03:23:31 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getPageDataSetList(String pageId) throws Exception
    {
        IData param = new DataMap();
        param.put("PAGE_ID", pageId);
        return Dao.qryByCode("TD_B_PAGE_DATASET", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-13 下午03:25:46 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getPageInfo(String pageId) throws Exception
    {
        IData param = new DataMap();
        param.put("PAGE_ID", pageId);
        return Dao.qryByCode("TD_B_PAGE", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-13 下午03:24:59 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getPageRuleSetList(String ruleSetId) throws Exception
    {
        IData param = new DataMap();
        param.put("RULESET_ID", ruleSetId);
        return Dao.qryByCode("TD_B_RULESET", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-13 下午02:57:37 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getPageSetPageList(String pageSetId) throws Exception
    {
        IData param = new DataMap();
        param.put("PAGESET_ID", pageSetId);
        return Dao.qryByCode("TD_B_PAGESET_PAGE", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-14 下午04:18:05 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-14 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getRoleBusiBtnList(String frameId, String roleCode) throws Exception
    {
        IData param = new DataMap();
        param.put("FRAME_ID", frameId);
        param.put("ROLE_CODE", roleCode);
        return Dao.qryByCode("TD_B_ROLE", "SEL_ROLE_BUTTON", param, Route.CONN_CRM_CEN);
    }
}
