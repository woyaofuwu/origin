
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ZfslValueQry
{
    public static IDataset queryAllByDiscntCode(IData param, Pagination page) throws Exception
    {
        IDataset set = Dao.qryByCodeParser("TD_B_ZFSL_VALUE", "SEL_BY_DISCNT_CODE", param, page, Route.CONN_CRM_CEN);
        return set;
    }

    public static IDataset queryAllByDiscntCode(String discntCode) throws Exception
    {
        IData param = new DataMap();
        param.put("DISCNT_CODE", discntCode);
        IDataset set = Dao.qryByCodeParser("TD_B_ZFSL_VALUE", "SEL_BY_DISCNT_CODE", param, Route.CONN_CRM_CEN);
        return set;
    }

    /**
     * 相似度查询
     * 
     * @param param
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset queryAllByRsrvStr(IData param, Pagination page) throws Exception
    {
        IDataset set = Dao.qryByCodeParser("TD_B_ZFSL_VALUE", "SEL_BY_DISCNT_RSRV_STR", param, page, Route.CONN_CRM_CEN);
        return set;
    }

    /**
     * 条件查询
     * 
     * @param param
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset queryAllByRsrvStr2(IData param, Pagination page) throws Exception
    {
        IDataset set = Dao.qryByCodeParser("TD_B_ZFSL_VALUE", "SEL_BY_DISCNT_RSRV_STR2", param, page, Route.CONN_CRM_CEN);
        return set;
    }

    // ADD BY HH
    public static IDataset queryZfslInfo(IData param) throws Exception
    {
        return Dao.qryByCodeParser("TD_B_ZFSL_VALUE", "SEL_BY_DISCNT_CODE", param);
    }
}
