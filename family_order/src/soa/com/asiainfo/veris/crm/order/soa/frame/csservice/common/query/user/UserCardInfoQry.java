
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserCardInfoQry
{

    public static IDataset qryUserCardFlowInfoBySn(String sn) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        return Dao.qryByCode("TF_F_SELFHELPCARD_FLOW", "SEL_BY_SN", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qryUserCardFlowInfoBySnAndTime(String sn, String sysDate, String endDate) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        param.put("START_DATE", sysDate);
        param.put("END_DATE", endDate);
        return Dao.qryByCode("TF_F_SELFHELPCARD_FLOW", "SEL_BY_SN_TIME", param, Route.CONN_CRM_CEN);
    }

    public static IData qryUserCardFlowInfoByTransId(String transId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRANS_ID", transId);
        IDataset ids = Dao.qryByCode("TF_F_SELFHELPCARD_FLOW", "SEL_BY_TRANSID", param, Route.CONN_CRM_CEN);
        if (IDataUtil.isNotEmpty(ids))
        {
            return ids.getData(0);
        }
        else
        {
            return null;
        }
    }
}
