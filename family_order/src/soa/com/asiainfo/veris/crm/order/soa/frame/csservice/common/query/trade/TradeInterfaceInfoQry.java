
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeInterfaceInfoQry
{

    // 批量修改数据
    public static void batUpdateTradeInfoState(IDataset UpdRecords) throws Exception
    {
        StringBuilder strSql = new StringBuilder();
        strSql.delete(0, strSql.length());
        strSql.append("UPDATE TI_B_TRADE_INTERFACE SET SUBSCRIBE_STATE = '1', UPDATE_TIME = SYSDATE WHERE TRADE_ID = :TRADE_ID");
        IDataset parames = new DatasetList();
        for (int i = 0; i < UpdRecords.size(); i++)
        {
            IData data = new DataMap();
            data.put("TRADE_ID", UpdRecords.getData(i).getString("TRADE_ID", ""));
            parames.add(data);
        }
        Dao.executeBatch(strSql, parames);
        SessionManager.getInstance().commit();
    }

    // ADD yangsh
    public static void deleteTradeByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        Dao.executeUpdateByCodeCode("TI_B_TRADE_INTERFACE", "DEL_BY_TRADEID", param);
    }

    // 批量处理 每次 60调. 可以在static表中配置
    // 查询数据
    public static IDataset getBatTradeInterfaceInfo() throws Exception
    {
        IDataset tradeInterfaces = Dao.qryByCode("TI_B_TRADE_INTERFACE", "SEL_TRADE_INTERFACE", null);
        return tradeInterfaces;

    }

    public static IDataset getBookTradeInterfaceInfoByUserId(String tradeTypeCode, String userId, String cancelTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("USER_ID", userId);
        param.put("CANCEL_TAG", cancelTag);
        return Dao.qryByCode("TI_B_TRADE_INTERFACE", "SEL_BOOK_INTERFACE_BY_USER_ID", param);

    }

    // 修改接口订单表中订单状态2 yangsh

    public static IDataset getTradeInterfaceInfo(String tradeId, String serialNumber, String execResult, String tradeTypeCode, String beginDate, String endDate, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("EXEC_RESULT", execResult);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("BEGIN_DATE", beginDate);
        param.put("END_DATE", endDate);
        return Dao.qryByCodeParser("TI_B_TRADE_INTERFACE", "SEL_TRADE_INTERFACE2", param, pagination);

    }

    // 是否有未完工单yangsh
    public static IDataset getTradeInterfaceInfoByUserId(String userId, String execTime) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("EXEC_TIME", execTime);
        IDataset tradeInterface = Dao.qryByCode("TI_B_TRADE_INTERFACE", "SEL_INTERFACE_BY_USER_ID", param);
        return tradeInterface;
    }

    // ADD yangsh
    public static void updateBHTradeByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("CANCEL_TAG", "0");
        Dao.executeUpdateByCodeCode("TI_BH_TRADE_INTERFACE", "INSERT_FROM_TRADE", param);
    }

    public static void updateStateTradeByTradeId(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TI_B_TRADE_INTERFACE", "UP_INTERFACE_BY_TRADEID2", param);
    }

    // 修改接口订单表中订单状态 yangsh
    // 是否有未完工单
    public static void updateTradeByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("SUBSCRIBE_STATE", "B");
        Dao.executeUpdateByCodeCode("TI_B_TRADE_INTERFACE", "UP_INTERFACE_BY_TRADEID", param);
    }
    
    public static void updateInterfaceStateByTradeId(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TI_B_TRADE_INTERFACE", "UP_INTERFACE_Y_STATE_BY_TRADEID", param);
    }

}
