
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

public class RestoreChangePhoneFinishAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset resTradeDataset = TradeResInfoQry.getTradeRes(tradeId, "0", BofConst.MODIFY_TAG_ADD);
        String userId = mainTrade.getString("USER_ID");
        String newPhoneNumber = "";
        if (IDataUtil.isNotEmpty(resTradeDataset))
        {
            IData resTradeData = resTradeDataset.getData(0);
            if (StringUtils.equals("1", resTradeData.getString("RSRV_TAG1")))
            {
                newPhoneNumber = resTradeData.getString("RES_CODE");
                if (StringUtils.isNotEmpty(newPhoneNumber))
                {
                    IData param = new DataMap();
                    param.put("SERIAL_NUMBER", newPhoneNumber);
                    param.put("USER_ID", userId);
                    Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "UPD_SN_BY_ID", param);
                }
            }
        }

    }

}
