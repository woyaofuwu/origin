
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class FinishCmnetCreateUserQry
{

    public static IDataset getTradeMainInfo(String tradetypecode, String serialnumber) throws Exception
    {

        IData input = new DataMap();
        input.put("TRADE_TYPE_CODE", tradetypecode);
        input.put("SERIAL_NUMBER", serialnumber);
        input.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        input.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        input.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());

        return Dao.qryByCode("TF_B_TRADE", "SEL_BY_CMNETUSER", input);
    }

    public static void UpdCmnet(String tradeid) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeid);
        param.put("TRADE_TYPE_CODE", "2026");
        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE SET SUBSCRIBE_STATE = 'P' ");
        parser.addSQL("WHERE　TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");
        parser.addSQL("AND TRADE_ID = :TRADE_ID ");
        Dao.executeUpdate(parser);
    }
}
