
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserChargeRemindInfoQry
{

    public static IDataset querrykChargeRemindBySeriaTrade(String SERIAL_NUMBER, String TRADE_ID) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SERIAL_NUMBER", SERIAL_NUMBER);
        cond.put("TRADE_ID", TRADE_ID);
        return Dao.qryByCode("TP_F_USER_CHARGEREMIND", "SEL_NOTDEAL_CHARGEREMIND_COUNTS", cond);
    }

    /**
     * 查询扣费提醒表
     * 
     * @param pd
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryUserChargeRemind(String TRADE_ID, String SERIAL_NUMBER) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SERIAL_NUMBER", SERIAL_NUMBER);
        cond.put("TRADE_ID", TRADE_ID);
        return Dao.qryByCode("TP_F_USER_CHARGEREMIND", "SEL_FOR_SMSANS", cond);
    }

    public static int updateUserChargeStatus(String TRADE_ID, String SERIAL_NUMBER, String STATUS, String FEE_FLAG, String CONTENT) throws Exception
    {
        IData cond = new DataMap();
        cond.put("TRADE_ID", TRADE_ID);
        cond.put("SERIAL_NUMBER", SERIAL_NUMBER);
        cond.put("STATUS", STATUS);
        cond.put("FEE_FLAG", FEE_FLAG);
        cond.put("CONTENT", CONTENT);
        return Dao.executeUpdateByCodeCode("TP_F_USER_CHARGEREMIND", "UPDATE_FOR_SMSANS", cond);
    }

}
