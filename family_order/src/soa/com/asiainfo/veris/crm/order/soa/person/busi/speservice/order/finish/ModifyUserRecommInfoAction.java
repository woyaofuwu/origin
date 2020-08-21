
package com.asiainfo.veris.crm.order.soa.person.busi.speservice.order.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

public class ModifyUserRecommInfoAction implements ITradeFinishAction
{
    private static final Logger logger = Logger.getLogger(ModifyUserRecommInfoAction.class);

    // 将已推荐业务信息写入备份表
    public void AddUserRecommToHistory(String TradeId, String UserId, String RecommType, String ElementId, String EparchyCode) throws Exception
    {
        IData tempData = new DataMap();
        tempData.put("USER_ID", UserId);
        tempData.put("RECOMM_TYPE", RecommType);
        tempData.put("ELEMENT_ID", ElementId);
        tempData.put("EPARCHY_CODE", EparchyCode);
        tempData.put("RECOMM_TRADE_ID", TradeId);
        Dao.executeUpdateByCodeCode("TF_F_USER_RECOMM", "INS_USER_RECOMM_HISTORY", tempData);// 备份信息

    }

    // 备份用户已推荐的新业务信息
    public void BackUserRecommInfo(IData mainTrade) throws Exception
    {

        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String disconId = mainTrade.getString("RSRV_STR3", "");
        String serviceId = mainTrade.getString("RSRV_STR1", "");
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");

        if (!"".equals(disconId)) // 含有推荐优惠信息
        {
            AddUserRecommToHistory(tradeId, userId, "1", disconId, eparchyCode);
            DelUserRecommInfo(tradeId, userId, "1", disconId, eparchyCode);
        }
        if (!"".equals(serviceId)) // 含有推荐服务信息
        {
            AddUserRecommToHistory(tradeId, userId, "2", serviceId, eparchyCode);
            DelUserRecommInfo(tradeId, userId, "2", serviceId, eparchyCode);
        }
    }

    public void dealOtherInfoAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset tradeOtherList = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId);
        int size = tradeOtherList.size();
        for (int i = 0; i < size; i++)
        {
            IData tradeOther = tradeOtherList.getData(i);
            IData dealParams = dealParams(tradeOther);
            dealSmCampaingAsked(dealParams);
        }
    }

    public IData dealParams(IData input) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SERV_ID", input.getString("USER_ID"));
        inparams.put("MSISDN", input.getString("RSRV_STR1"));
        inparams.put("EPARCHY_CODE", input.getString("RSRV_STR2"));
        inparams.put("DEPART_ID", input.getString("DEPART_ID"));
        inparams.put("STAFF_ID", input.getString("STAFF_ID"));
        inparams.put("MARKET_ID", input.getString("RSRV_STR14"));
        inparams.put("MARKET_NAME", input.getString("RSRV_STR4"));
        inparams.put("OPER_TYPE", input.getString("RSRV_STR12"));
        inparams.put("OPER_CODE", input.getString("RSRV_STR7"));
        inparams.put("ACCEPT_TIME", input.getString("START_DATE"));
        inparams.put("ACCEPT_FLAG", "1");

        inparams.put("EFFECT_CODE", input.getString("RSRV_STR5"));
        inparams.put("REMARK", input.getString("RSRV_STR10"));
        inparams.put("CHANNLE_TYPE", "T000");
        inparams.put("REFUSE_CODE", input.getString("RSRV_STR11"));// 拒绝编码
        inparams.put("REFUSE_REASON_DESC", input.getString("RSRV_STR13"));// 拒绝原因
        inparams.put("TRADE_ID", input.getString("TRADE_ID"));
        inparams.put("CAMPN_ID", input.getString("RSRV_STR3"));

        return inparams;
    }

    public void dealSmCampaingAsked(IData input) throws Exception
    {
        Dao.insert("TF_SM_CAMPAIGN_ASKED_RESULT", input);
    }

    // 删除已推荐的新业务信息
    public void DelUserRecommInfo(String TradeId, String UserId, String RecommType, String ElementId, String EparchyCode) throws Exception
    {
        IData tempData = new DataMap();
        tempData.put("USER_ID", UserId);
        tempData.put("RECOMM_TYPE", RecommType);
        tempData.put("ELEMENT_ID", ElementId);
        tempData.put("EPARCHY_CODE", EparchyCode);
        tempData.put("RECOMM_TRADE_ID", TradeId);
        Dao.executeUpdateByCodeCode("TF_F_USER_RECOMM", "DEL_USER_RECOMM_INFO", tempData);// 备份信息

    }

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("------ModifyUserRecommInfoAction-----mainTrade start-----------" + mainTrade);

        dealOtherInfoAction(mainTrade);

        // // 修改用户新业务推荐信息
        // modifyUserRecommInfo(mainTrade);
        // // 备份用户已推荐的新业务信息
        // BackUserRecommInfo(mainTrade);
    }

    /**
     * 修改用户新业务推荐信息
     * 
     * @data 2013-8-3
     * @param mainTrade
     */
    public void modifyUserRecommInfo(IData mainTrade) throws Exception
    {

        IData tempData = new DataMap();
        if (!"".equals(mainTrade.getString("RSRV_STR1", ""))) // 推荐了产品信息
        {
            tempData.clear();
            tempData.put("RECOMM_TYPE", "2");// 推荐类型 2=服务
            tempData.put("ELEMENT_ID", mainTrade.getString("RSRV_STR1"));// 推荐产品编码
            tempData.put("REPLY_CODE", mainTrade.getString("RSRV_STR2"));// 推荐产品结果
            tempData.put("RECOMM_TAG", "0");
            tempData.put("USER_ID", mainTrade.getString("USER_ID"));
            tempData.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE"));
            tempData.put("RECOMM_TRADE_ID", mainTrade.getString("TRADE_ID"));// 推荐关联流水
            tempData.put("RECOMM_DATE", mainTrade.getString("UPDATE_TIME"));// 推荐日期
            tempData.put("RECOMM_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));// 推荐员工
            tempData.put("RECOMM_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));// 推荐员工部门
            tempData.put("REPLY_NOTES", mainTrade.getString("REMARK"));// 备注

            Dao.executeUpdateByCodeCode("TF_F_USER_RECOMM", "UPD_USER_RECOMM_INFO", tempData);
        }

        if (!"".equals(mainTrade.getString("RSRV_STR3", ""))) // 推荐了优惠信息
        {
            tempData.clear();
            tempData.put("RECOMM_TYPE", "1");// 推荐类型 1=优惠
            tempData.put("ELEMENT_ID", mainTrade.getString("RSRV_STR3"));// 推荐优惠编码
            tempData.put("REPLY_CODE", mainTrade.getString("RSRV_STR4"));// 推荐优惠结果
            tempData.put("RECOMM_TAG", "0");
            tempData.put("USER_ID", mainTrade.getString("USER_ID"));
            tempData.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE"));
            tempData.put("RECOMM_TRADE_ID", mainTrade.getString("TRADE_ID"));// 推荐关联流水
            tempData.put("RECOMM_DATE", mainTrade.getString("UPDATE_TIME"));// 推荐日期
            tempData.put("RECOMM_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));// 推荐员工
            tempData.put("RECOMM_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));// 推荐员工部门
            tempData.put("REPLY_NOTES", mainTrade.getString("REMARK"));// 备注
            Dao.executeUpdateByCodeCode("TF_F_USER_RECOMM", "UPD_USER_RECOMM_INFO", tempData);
        }

    }

}
