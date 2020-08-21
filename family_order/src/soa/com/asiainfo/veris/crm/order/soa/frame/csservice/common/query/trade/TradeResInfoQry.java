
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeResInfoQry
{
    /**
     * 根据tradeId查询所有的用户资源备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakResByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_RES_BAK", "SEL_BY_TRADE", params);
    }

    public static IDataset getTradeRes(String tradeId, String resTypeCode, String modifyTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("RES_TYPE_CODE", resTypeCode);
        param.put("MODIFY_TAG", modifyTag);
        return Dao.qryByCode("TF_B_TRADE_RES", "SEL_BY_RESTYPECODE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static IDataset getTradeResByTradeIdAndModify(String tradeId, String modifyTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("MODIFY_TAG", modifyTag);
        return Dao.qryByCode("TF_B_TRADE_RES", "SEL_BY_MODIFYTAG", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    /**
     * 根据trade_id、res_type_code、modify_tag获取用户资源台账信息
     * 
     * @param trade_id
     * @param res_type_code
     * @param modify_tag
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeResByTradeTypeMtag(String trade_id, String res_type_code, String modify_tag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("RES_TYPE_CODE", res_type_code);
        param.put("MODIFY_TAG", modify_tag);

        return Dao.qryByCodeParser("TF_B_TRADE_RES", "SEL_TRADEID_0", param, Route.getJourDb());
    }

    public static IDataset qryTradeResInfosByType(String trade_id, String res_type_code, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("RES_TYPE_CODE", res_type_code);
        IDataset infos = Dao.qryByCodeParser("TF_B_TRADE_RES", "SEL_TRADERES_BY_RESTYPE", param, pagination,Route.getJourDb());
        return infos;
    }

    /**
     * 根据trade_id查询所有资源台账 liuke
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryAllTradeResByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_RES", "SEL_ALL_BY_TRADE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static IDataset queryTradeRes(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_RES", "INS_SEL_TRADE_RES", param);
    }

    /**
     * 根据trade_id查询所有资源台账 liuke
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeResByTradeIdAndModifyTag(String tradeId, String modifyTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("MODIFY_TAG", modifyTag);
        return Dao.qryByCode("TF_B_TRADE_RES", "SEL_TRADEID", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static IDataset queryUpdtradeRes(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_RES", "UPD_SEL_TRADE_RES", param);
    }

    /**
     * 根据tradeId查询备份的有效资源数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryValidTradeResBakByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_RES_BAK", "SEL_VALID_TRADERES_BAK", param);
    }

    public static void updateStartDate(String tradeId, String startDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("START_DATE", startDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_RES", "UPD_STARTDATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    public static IDataset getSelfHelpDeviceInfoByDeviceId(String resNo, String removeTag) throws Exception
    {
        IData param = new DataMap();
        param.put("DEVICE_ID", resNo);
        param.put("REMOVE_TAG", removeTag);
        return Dao.qryByCodeParser("TF_R_SELF_HELP_DEVICE", "SEL_DEVICE_BY_DEVICE_ID", param);
    }
    
    public static IDataset getSelfHelpDeviceInfoByStaffId(String resNo, String removeTag) throws Exception
    {
        IData param = new DataMap();
        param.put("DEVICE_STAFF_ID", resNo);
        param.put("REMOVE_TAG", removeTag);
        return Dao.qryByCodeParser("TF_R_SELF_HELP_DEVICE", "SEL_DEVICE_BY_STAFF_ID", param);
    }
}
