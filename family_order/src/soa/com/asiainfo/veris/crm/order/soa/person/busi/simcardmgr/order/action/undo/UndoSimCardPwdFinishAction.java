
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.undo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;

/**
 * 密码卡返销
 */
public class UndoSimCardPwdFinishAction implements ITradeFinishAction
{
    protected static Logger log = Logger.getLogger(UndoSimCardPwdFinishAction.class);

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        IDataset userTradeInfos = TradeUserInfoQry.getTradeUserByTradeId(tradeId);

        if (IDataUtil.isNotEmpty(userTradeInfos))
        {
            for (int i = 0; i < userTradeInfos.size(); i++)
            {
                if ("1".equals(userTradeInfos.getData(i).getString("RSRV_TAG1")))
                {// 使用密码卡
                    IData data = new DataMap();
                    data.put("USER_ID", userId);
                    Dao.executeUpdateByCodeCode("TF_F_USER_ENCRYPT_GENE", "DEL_BY_USERID", data);
                    if ("142".equals(mainTrade.getString("TRADE_TYPE_CODE")))
                    {
                        data.put("ENCRYPT_GENE", userTradeInfos.getData(i).getString("RSRV_STR4"));
                        Dao.executeUpdateByCodeCode("TF_F_USER_ENCRYPT_GENE", "INS_ENCRYPT", data);
                    }
                }
            }
        }

    }

}
