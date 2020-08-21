
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryUserPostRepairQry extends CSBizBean
{
    public static IDataset queryUserPostRepair(String processTag, String startTime, String endDate, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("SERVICE_MODE", "PR");
        indata.put("PROCESS_TAG", processTag);
        indata.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        indata.put("RSRV_DATE2", startTime);
        indata.put("EPARCHY_CODE", routeEparchyCode);
        indata.put("RSRV_DATE3", endDate);// 查询的结束时间
        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_SRVMODE_POST", indata, pagination, routeEparchyCode);
    }

    public static int updPostRepair(String rsrvStr1, String processRemark, String routeEparchyCode) throws Exception
    {
        IData params = new DataMap();
        StringBuilder strSql = new StringBuilder();
        params.put("PROCESS_TAG", "0");
        params.put("SERVICE_MODE", "PR");
        params.put("REMARK", processRemark);
        strSql.delete(0, strSql.length());
        strSql.append("UPDATE tf_f_user_otherserv SET rsrv_date2=SYSDATE,process_tag='1',remark=:REMARK WHERE rsrv_str1 IN(");
        strSql.append(rsrvStr1);
        strSql.append(")");
        strSql.append(" AND process_tag=:PROCESS_TAG AND service_mode=:SERVICE_MODE");
        return Dao.executeUpdate(strSql, params, routeEparchyCode);

    }
}
