
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DestroyUpdateLimitBlackWhiteAction.java
 * @Description: 销户更新TF_F_LIMIT_BLACKWHITE表数据
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-5 下午9:07:22
 */
public class DestroyUpdateLimitBlackWhiteAction implements ITradeFinishAction
{
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset userTradeDataset = TradeUserInfoQry.getTradeUserByTradeId(tradeId);
        if (IDataUtil.isEmpty(userTradeDataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_225);
        }
        IData userTradeData = userTradeDataset.getData(0);
        String userEparchyCode = userTradeData.getString("EPARCHY_CODE");
        String userId = userTradeData.getString("USER_ID");
        IData param = new DataMap();
        param.put("USER_ID", userId);
        Dao.executeUpdateByCodeCode("TF_F_LIMIT_BLACKWHITE", "UPD_LIMIT_BLACKWHITE", param, userEparchyCode);
    }
}
