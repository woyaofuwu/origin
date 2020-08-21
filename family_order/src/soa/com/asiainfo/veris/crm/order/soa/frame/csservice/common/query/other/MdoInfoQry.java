
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class MdoInfoQry
{
    /**
     * @param userId
     * @param qryId
     * @param startDate
     * @param endDate
     * @param page
     * @return
     * @throws Exception
     * @author wangww
     */
    public static IDataset getMdoIvrOneInfo(String userId, String qryId, String startDate, String endDate, Pagination page) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("QRY_ID", qryId);
        inparam.put("RSRV_STR1", startDate);
        inparam.put("RSRV_STR2", endDate);
        return Dao.qryByCode("TF_F_MDO_QUERY_RESULT", "SEL_USER_MDO_IVR", inparam, page);
    }

    /**
     * @param userId
     * @param qryId
     * @param startDate
     * @param endDate
     * @param page
     * @return
     * @throws Exception
     * @author wangww
     */
    public static IDataset getMdoIvrTwoInfo(String userId, String qryId, String startDate, String endDate, Pagination page) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("QRY_ID", qryId);
        inparam.put("RSRV_STR1", startDate);
        inparam.put("RSRV_STR2", endDate);
        return Dao.qryByCode("TF_F_MDO_QUERY_RESULT", "SEL_USER_MDO_IVR_OTHER", inparam, page);
    }

    /**
     * 查询用户订购mdo信息
     */
    public static IDataset queryUserMdoSvcInfos(String userId, String queryType, String qryId, String startDate, String endDate, String mdoQueryMode, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("QUERY_TYPE", queryType);
        inparam.put("QRY_ID", qryId);
        inparam.put("RSRV_STR1", startDate);
        inparam.put("RSRV_STR2", endDate);
        inparam.put("RSRV_STR3", mdoQueryMode);
        return Dao.qryByCode("TF_F_MDO_QUERY_RESULT", "SEL_USER_MDO", inparam, pagination);
    }

    /**
     * 查询用户订购mdo信息
     */
    public static IDataset queryUserMdoSvcOtherInfos(String userId, String queryType, String qryId, String startDate, String endDate, String mdoQueryMode, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("QUERY_TYPE", queryType);
        inparam.put("QRY_ID", qryId);
        inparam.put("RSRV_STR1", startDate);
        inparam.put("RSRV_STR2", endDate);
        inparam.put("RSRV_STR3", mdoQueryMode);
        return Dao.qryByCode("TF_F_MDO_QUERY_RESULT", "SEL_USER_MDO_OTHER", inparam, pagination);
    }
}
