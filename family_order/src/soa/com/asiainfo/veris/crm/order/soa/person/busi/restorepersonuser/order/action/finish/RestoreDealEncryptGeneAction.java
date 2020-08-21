
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEncryptGeneInfoQry;

public class RestoreDealEncryptGeneAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset userTradeDataset = TradeUserInfoQry.getTradeUserByTradeId(tradeId);
        String userId = mainTrade.getString("USER_ID");
        if (IDataUtil.isNotEmpty(userTradeDataset))
        {
            IData userTradeData = userTradeDataset.getData(0);
            if (StringUtils.equals("1", userTradeData.getString("RSRV_TAG1")))// 密码卡
            {
                String passCode = userTradeData.getString("RSRV_STR3");
                if (StringUtils.isNotEmpty(passCode))
                {
                    IData data = new DataMap();
                    data.put("USER_ID", userId);
                    Dao.executeUpdateByCodeCode("TF_F_USER_ENCRYPT_GENE", "DEL_BY_USERID", data);

                    data.put("ENCRYPT_GENE", passCode);
                    Dao.executeUpdateByCodeCode("TF_F_USER_ENCRYPT_GENE", "INS_ENCRYPT", data);
                    UserEncryptGeneInfoQry.updataPswByUserid(userId, passCode);
                }
            }
        }
    }
}
