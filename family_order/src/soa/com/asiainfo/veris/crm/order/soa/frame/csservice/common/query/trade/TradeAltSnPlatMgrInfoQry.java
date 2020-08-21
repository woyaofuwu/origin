
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeAltSnPlatMgrInfoQry
{

    public static boolean insPreAltPlatTrade(String tradeId, String serialNumber, String relaSerialNumber, String relaType, String updateTime, String platSvcId, String needTransfer, String isLocalBase, String eparchyCode) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("TRADE_ID", tradeId);
        inparam.put("MONTH", StrUtil.getAcceptMonthById(tradeId));
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("RELA_SERIAL_NUMBER", relaSerialNumber);
        inparam.put("RELA_TYPE", relaType);
        inparam.put("PRE_ACCEPT_TIME", updateTime);
        inparam.put("PLAT_SVC_ID", platSvcId);
        inparam.put("NEED_TRANSFER", needTransfer);
        inparam.put("IS_LOCAL_BASE", isLocalBase);
        inparam.put("EPARCHY_CODE", eparchyCode);

        return Dao.insert("TF_B_TRADE_ALTSN_PLATMRG", inparam);
    }
}
