
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.sync;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ExpireSyncUserSVC.java
 * @Description: 到期提醒处理TI_B_USER同步【适用于产品变更业务】
 * @version: v1.0.0
 * @author: maoke
 * @date: Sep 6, 2014 3:54:06 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Sep 6, 2014 maoke v1.0.0 修改原因
 */
public class ExpireSyncUserSVC extends CSBizService
{
    public void dealSync(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");

        // 查历史台账 如存在未返销的 才进行处理
        IData mainHiTrade = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);

        if (IDataUtil.isNotEmpty(mainHiTrade))
        {
            String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

            // 产品变更TI_B_USER同步处理
            this.syncUserInfo(tradeId, userId, tradeTypeCode);
        }
    }

    /**
     * @Description: 是否产品变更
     * @param tradeId
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Aug 19, 2014 10:51:42 AM
     */
    public boolean isProductChange(String tradeId) throws Exception
    {
        IDataset productTrade = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
        if (IDataUtil.isNotEmpty(productTrade))
        {
            for (int i = 0; i < productTrade.size(); i++)
            {
                if ("1".equals(productTrade.getData(i).getString("MAIN_TAG")))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @Description: 同步用户主产品、品牌信息给账务侧
     * @param tradeId
     * @param userId
     * @throws Exception
     * @author: maoke
     * @date: Aug 19, 2014 2:57:07 PM
     */
    public void syncUserInfo(String tradeId, String userId, String tradeTypeCode) throws Exception
    {
        if (this.isProductChange(tradeId))
        {
            String syncSequence = SeqMgr.getSyncIncreId();
            String curDay = SysDateMgr.getCurDay();
            IDataset UserInfos=UserInfoQry.getUserInfoForSyncUser(userId);
            if(IDataUtil.isNotEmpty(UserInfos)){
            	for(int i=0;i<UserInfos.size();i++){
            		 IData param = UserInfos.getData(i);
            		 param.put("SYNC_SEQUENCE", syncSequence);
              	     param.put("USER_ID", userId);
              	     param.put("MODIFY_TAG", "8");
              	     param.put("TRADE_ID", tradeId);
            	     Dao.executeUpdateByCodeCode("TI_B_USER", "INS_TI_B_USER", param,Route.getJourDb(BizRoute.getRouteId()));
            	}
            } 
            IData syncData = new DataMap();
            syncData.put("SYNC_SEQUENCE", syncSequence);
            syncData.put("SYNC_DAY", curDay);
            syncData.put("SYNC_TYPE", "0");
            syncData.put("TRADE_ID", tradeId);
            syncData.put("STATE", "0");
            syncData.put("REMARK", UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode) + "到期提醒用户产品品牌同步【USER_ID=" + userId + "】");

            Dao.insert("TI_B_SYNCHINFO", syncData,Route.getJourDb(BizRoute.getRouteId()));
        }
    }
}
