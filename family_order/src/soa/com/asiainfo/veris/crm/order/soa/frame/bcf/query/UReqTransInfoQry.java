
package com.asiainfo.veris.crm.order.soa.frame.bcf.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class UReqTransInfoQry
{
    /**
     * 查询所有接口请求转换配置
     * 
     * @return
     * @throws Exception
     */
    public static IDataset qryAllReqTrans() throws Exception
    {
        IData param = new DataMap();

        IDataset ids = Dao.qryByCode("TD_B_REQUESTTRANS", "SEL_REQUESTTRANS_ALL", param, Route.CONN_CRM_CEN);

        return ids;
    }

    /**
     * 根据服务名称和接入渠道查询接口请求转换配置
     * 
     * @param xTransCode
     * @param in_mode_code
     * @return
     * @throws Exception
     */
    public static IDataset qryReqtTransByPK(String xTransCode, String in_mode_code) throws Exception
    {
        IData param = new DataMap();
        param.put("X_TRANS_CODE", xTransCode);
        param.put("IN_MODE_CODE", in_mode_code);

        IDataset ids = Dao.qryByCode("TD_B_REQUESTTRANS", "SEL_REQUESTTRANS_BY_PK", param, Route.CONN_CRM_CEN);

        return ids;
    }
}
