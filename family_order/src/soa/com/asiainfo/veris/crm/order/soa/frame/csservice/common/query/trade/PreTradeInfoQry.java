
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PreTradeInfoQry
{

    public static boolean insPreMainTrade(String tradeId, String tradeTypeCode, String acceptTime, String invalidTime, String serialNumber, String inModeCode, String updateTime, String staffId, String departId, String rsrvStr1, String rsrvStr6)
            throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("TRADE_ID", tradeId);
        inparam.put("MONTH", StrUtil.getAcceptMonthById(tradeId));
        inparam.put("TRADE_TYPE_CODE", tradeTypeCode);// "800" ) ;
        inparam.put("PRE_ACCEPT_TIME", acceptTime);
        inparam.put("PRE_INVALID_TIME", invalidTime);
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("STATUS", "1"); // 1：预受理登记成功 2：预受理取消 3：预受理对应业务成功
        inparam.put("IN_MODE_CODE", inModeCode);
        inparam.put("UPDATE_TIME", updateTime);
        inparam.put("UPDATE_STAFF_ID", staffId);// 依下面这行的问题同步修改 pd.getData().getString("TRADE_STAFF_ID", "SUPERUSR" )) ;
        inparam.put("UPDATE_DEPART_ID", departId);// 新疆发现的问题 2013-1-8 pd.getData().getString("0TRADE_DEPART_ID","1111")

        inparam.put("RSRV_STR1", rsrvStr1);
        inparam.put("RSRV_STR6", rsrvStr6);

        return Dao.insert("TF_B_PRE_TRADE", inparam);
    }

    public static IDataset queryAltCardPreInfo(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_B_PRE_TRADE", "SEL_FOR_QRY_REQ", param);

    }

    public static IDataset queryPreStatus(String serialNumber, String trade_type_code) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_TYPE_CODE", trade_type_code);
        return Dao.qryByCode("TF_B_PRE_TRADE", "QRY_PRE_STATUS", param);
    }
    
    public static IDataset queryPreStatus1(String serialNumber, String trade_type_code) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_TYPE_CODE", trade_type_code);
        return Dao.qryByCode("TF_B_PRE_TRADE", "QRY_PRE_STATUS1", param);
    }

    public static IDataset queryTradeInfoBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_B_PRE_TRADE", "SEL_BY_UNFINISH_VALID_TRADE", param);
    }
    
    public static IDataset queryTradeInfoBySn1(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_B_PRE_TRADE", "SEL_BY_UNFINISH_VALID_TRADE1", param);
    }
 
}
