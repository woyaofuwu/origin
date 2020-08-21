
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserChangeCardFlowInfoQry
{

    public static IDataset checkFlowTimeOut(String transId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRANS_ID", transId);

        return Dao.qryByCode("TF_F_SELFHELPCARD_FLOW", "SEL_BY_TRANSID_TIMEOUT", param);
    }

    public static IDataset checkTimeOut(String transId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRANS_ID", transId);
        return Dao.qryByCode("TF_F_SELFHELPCARD_FLOW", "SEL_BY_TRANSID_TIMEOUT", params);
    }

    public static int delByTransId(String transId, String startDate) throws Exception
    {
        IData params = new DataMap();
        params.put("TRANS_ID", transId);
        params.put("START_DATE", startDate);
        return Dao.executeUpdateByCodeCode("TF_F_SELFHELPCARD_FLOW", "DEL_BY_TRANSID_TIME", params);
    }

    public static IData qryUserCardFlowInfoByIBossId(String rsrvStr2) throws Exception
    {
        IData params = new DataMap();
        params.put("RSRV_STR2", rsrvStr2);
        SQLParser sql = new SQLParser(params);
        sql.addSQL("SELECT to_char(trans_id) trans_id,user_id,serial_number,serial_number_temp,eparchy_code,state,TO_CHAR(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,"
                + "TO_CHAR(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,TO_CHAR(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,imsi_new,pin_new,pin2_new,puk_new,puk2_new,"
                + "ki_new,opc_new,sim_card_no_new,imsi_old,sim_card_no_old,ki_old,opc_old,opc_temp,ki_temp,imsi_temp,pin_temp,pin2_temp,puk_temp,puk2_temp,sim_card_no_temp,"
                + "empty_card_id,remark,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_str6,rsrv_str7");
        sql.addSQL(" from TF_F_SELFHELPCARD_FLOW where 1=1");
        sql.addSQL(" and rsrv_str2 = :RSRV_STR2");
        IDataset ids = Dao.qryByParse(sql);
        if (IDataUtil.isNotEmpty(ids))
        {
            return ids.getData(0);
        }
        else
        {
            return null;
        }
    }

    public static IDataset qryUserCardFlowInfoBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_F_SELFHELPCARD_FLOW", "SEL_BY_SN", param);
    }

    public static IDataset qryUserCardFlowInfoBySnAndTempSn(String serialNumber, String serilNumberTemp, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("SERIAL_NUMBER_TEMP", serilNumberTemp);

        return Dao.qryByCode("TF_F_SELFHELPCARD_FLOW", "SEL_BY_KEFU", param, pagination);
    }

    public static IDataset qryUserCardFlowInfoByTempSn(String tempNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_TEMP", tempNumber);

        return Dao.qryByCode("TF_F_SELFHELPCARD_FLOW", "SEL_BY_TEMPSN", param);
    }

    public static IDataset qryUserCardFlowInfoByTransId(String transId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRANS_ID", transId);

        return Dao.qryByCode("TF_F_SELFHELPCARD_FLOW", "SEL_BY_TRANSID", param);
    }

    public static int updByTransId(String dealReason, String staffId, String transId, String startDate) throws Exception
    {
        IData params = new DataMap();
        params.put("DEL_REASON", dealReason);
        params.put("STAFF_ID", staffId);
        params.put("TRANS_ID", transId);
        params.put("START_DATE", startDate);
        return Dao.executeUpdateByCodeCode("TF_F_SELFHELPCARD_FLOW", "UPD_TO_HISTORY_TIME", params);
    }

}
