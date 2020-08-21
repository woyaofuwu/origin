
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeSvcStateInfoQry
{
    /**
     * 根据tradeId查询所有的账户信息备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakSvcStateByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_SVCSTATE_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    /**
     * 根据tradeId、mainTag、modifyTag查询服务状态变化订单数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset querySvcStateBySvcIdAndOldState(String tradeTypeCode, String brandCode, String productId, String eparchyCode, String svcId, String oldState) throws Exception
    {
        IData cond = new DataMap();
        cond.put("TRADE_TYPE_CODE", tradeTypeCode);
        cond.put("BRAND_CODE", brandCode);
        cond.put("PRODUCT_ID", productId);
        cond.put("EPARCHY_CODE", eparchyCode);
        cond.put("SERVICE_ID", svcId);
        cond.put("OLD_STATE_CODE", oldState);
        return Dao.qryByCode("TD_S_TRADE_SVCSTATE", "SEL_BY_SVCID_OLDSTATE", cond, Route.CONN_CRM_CEN);
    }

    /**
     * 根据tradeId、mainTag、modifyTag查询服务状态变化订单数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeMainSvcState(String tradeId, String mainTag, String modifyTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("MAIN_TAG", mainTag);
        param.put("MODIFY_TAG", modifyTag);
        return Dao.qryByCode("TF_B_TRADE_SVCSTATE", "SEL_BY_MAIN", param);
    }

    public static IDataset queryTradeSvcsStateByUserId(String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_SVCSTATE", "SEL_BY_USERID", param, pagination);
    }

    /**
     * 根据tradeId获取服务状态信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeSvcStateByTrade(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_SVCSTATE", "SEL_BY_TRADEID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    /**
     * 根据tradeId、userId、mainTag、modifyTag查询服务状态变化订单数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeSvcStateByTradeAndUserId(String tradeId, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_SVCSTATE", "SEL_BY_TRADEID_MAINTAG", param);
    }

    public static IDataset queryTradeUserSvcState(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_SVCSTATE", "INS_SEL_TRADE_SVCSTATE", param);
    }

    /**
     * @description 查询是否存在有效的宽带状态变更
     * @author chenzm
     * @param modifyTag
     * @param mainTag
     * @param userId
     * @param cancelTag
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeWidenetUserMainSvcState(String modifyTag, String mainTag, String userId, String cancelTag) throws Exception
    {
        IData param = new DataMap();
        param.put("MODIFY_TAG", modifyTag);
        param.put("MAIN_TAG", mainTag);
        param.put("USER_ID", userId);
        param.put("CANCEL_TAG", cancelTag);
        return Dao.qryByCode("TF_B_TRADE_SVCSTATE", "SEL_MAINSTATE_BY_BOOKTRADE", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    /**
     * @description 查询是否存在有效的宽带状态变更
     * @author chenzm
     * @param modifyTag
     * @param mainTag
     * @param userId
     * @param cancelTag
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeWidenetUserMainSvcState(String mainTag, String userId, String cancelTag) throws Exception
    {
        IData param = new DataMap();
        param.put("MAIN_TAG", mainTag);
        param.put("USER_ID", userId);
        param.put("CANCEL_TAG", cancelTag);
        return Dao.qryByCode("TF_B_TRADE_SVCSTATE", "SEL_MAINSTATE_BY_BOOKTRADE_2", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    /**
     * @description 查询是否有未完成的宽带停机工单
     * @author chenzm
     * @param modifyTag
     * @param mainTag
     * @param userId
     * @param cancelTag
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeWidenetUserSvcState(String modifyTag, String mainTag, String userId, String cancelTag) throws Exception
    {
        IData param = new DataMap();
        param.put("MODIFY_TAG", modifyTag);
        param.put("MAIN_TAG", mainTag);
        param.put("USER_ID", userId);
        param.put("CANCEL_TAG", cancelTag);
        return Dao.qryByCode("TF_B_TRADE_SVCSTATE", "SEL_STATE_BY_BOOK_TRADE", param);
    }

    public static IDataset queryUpdTradeRes(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_SVCSTATE", "UPD_SEL_TRADE_RES", param);
    }
}
