
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.telephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TelInfoQry extends CSBizBean
{

    /**
     * 根据userid查询固话信息
     * 
     * @author dengyong3
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static IDataset getTelInfo(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_TELEPHONE", "SEL_BY_USERID", data);
    }

    /**
     * 根据TRADE_id查询固话台账表
     * 
     * @author dengyong3
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static IDataset getTelTradeInfo(String tradeId) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_TELEPHONE", "SEL_BY_TRADEID", data,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    /**
     * 插入TF_F_USER_TRUNK表
     * 
     * @author dengyong3
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static int insertTrunk(String rsrv1, String rsrv2, int rsrvNum1, String userId, String startDate, String instId, String updateStaffId, String updateDepartId, String remark) throws Exception
    {
        IData data = new DataMap();
        data.put("RSRV_STR1", rsrv1);
        data.put("RSRV_STR2", rsrv2);
        data.put("RSRV_NUM1", rsrvNum1);
        data.put("USER_ID", userId);
        data.put("START_DATE", startDate);
        data.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
        data.put("INST_ID", instId);
        data.put("UPDATE_STAFF_ID", updateStaffId);
        data.put("UPDATE_DEPART_ID", updateDepartId);
        data.put("REMARK", remark);
        return Dao.executeUpdateByCodeCode("TF_F_USER", "INS_USER_TRUNK", data);
    }

    /**
     * 修改telephone备用字段123
     * 
     * @author dengyong3
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static int updateTeleRsrv123(String instId, String userId, String rsrv1, String rsrv2, String rsrv3,String tradeId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("INST_ID", instId);
        data.put("TRADE_ID", tradeId);
        data.put("RSRV_STR1", rsrv1);
        data.put("RSRV_STR2", rsrv2);
        data.put("RSRV_STR3", rsrv3);
        return Dao.executeUpdateByCodeCode("TF_B_TRADE_TELEPHONE", "UPD_RSRVSTR123", data, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

}
