
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.biz.bean.BizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * IP直通车工作记录查询
 * 
 * @author steven
 */
public class IpExpressTradeLogQry extends CSBizBean
{
    /**
     * '1','按固定号码查询'
     * 
     * @param serialNumber
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryIPExpressLogByFixedNo(String serialNumber, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("PARA_CODE1", serialNumber);
        params.put("PARA_CODE2", null);
        params.put("PARA_CODE3", null);
        params.put("PARA_CODE4", null);
        params.put("PARA_CODE5", null);
        params.put("PARA_CODE6", null);
        params.put("PARA_CODE7", null);
        params.put("PARA_CODE8", null);
        params.put("PARA_CODE9", null);
        params.put("PARA_CODE10", null);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_IPEXPRESSTRADELOG2_NEW", params, pagination, routeEparchyCode);
    }

    /**
     * '0','按手机号码查询
     * 
     * @param serialNumber
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryIPExpressLogByMobileNo(String serialNumber, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("PARA_CODE1", serialNumber);
        params.put("PARA_CODE2", null);
        params.put("PARA_CODE3", null);
        params.put("PARA_CODE4", null);
        params.put("PARA_CODE5", null);
        params.put("PARA_CODE6", null);
        params.put("PARA_CODE7", null);
        params.put("PARA_CODE8", null);
        params.put("PARA_CODE9", null);
        params.put("PARA_CODE10", null);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_IPEXPRESSTRADELOG1_NEW", params, pagination, Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
    }

    /**
     * '2','按员工工号查询'
     * 
     * @param staffId
     * @param startDate
     * @param endDate
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryIPExpressLogByStaffId(String staffId, String startDate, String endDate, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("PARA_CODE1", staffId);
        params.put("PARA_CODE2", startDate);
        params.put("PARA_CODE3", endDate);
        params.put("PARA_CODE4", null);
        params.put("PARA_CODE5", null);
        params.put("PARA_CODE6", null);
        params.put("PARA_CODE7", null);
        params.put("PARA_CODE8", null);
        params.put("PARA_CODE9", null);
        params.put("PARA_CODE10", null);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_IPEXPRESSTRADELOG3_NEW", params, pagination, routeEparchyCode);
    }
}
