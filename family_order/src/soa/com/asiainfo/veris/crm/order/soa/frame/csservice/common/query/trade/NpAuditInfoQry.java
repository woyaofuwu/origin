
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class NpAuditInfoQry
{

    public static IDataset qryNpAuditInfos(String npsysid) throws Exception
    {
        IData param = new DataMap();
        param.put("NPSYSID", npsysid);
        return Dao.qryByCode("TF_B_NP_AUDIT", "SEL_BY_PK", param, Route.CONN_UIF);
        // return null;
    }

    public static IDataset qryNpAuditInfos(String serviceType, String state, String createTime, String npcodeList, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_TYPE", serviceType);
        param.put("STATE", state);
        param.put("CREATE_TIME", createTime);
        param.put("NPCODE_LIST", npcodeList);
        return Dao.qryByCodeParser("TF_B_NP_AUDIT", "SEL_BY_SERVICETYPE", param, page, Route.CONN_UIF);
        // return null;
    }
}
