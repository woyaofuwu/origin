
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acctfeetradereg;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeFeeRegQry
{

    /**
     * 费用子台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeFeesubByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("FEE_MODE", "1");
        return Dao.qryByCode("TF_B_TRADEFEE_SUB", "SEL_BY_TRADE", param,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 用户押金
     * 
     * @param userId
     * @param foregiftCode
     * @return
     * @throws Exception
     */
    public static IData getUserForegift(String userId, String foregiftCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("FOREGIFT_CODE", foregiftCode);
        IDataset foregifts = Dao.qryByCode("TF_F_USER_FOREGIFT", "SEL_BY_USER_TYPE", param);
        return (foregifts != null && foregifts.size() > 0) ? foregifts.getData(0) : new DataMap();
    }

    // 押金转预存
    public static IDataset getUserOtherServBuf(String userId, String foregiftCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_MODE", "FG");
        param.put("PROCESS_TAG", "0");
        param.put("RSRV_NUM1", foregiftCode);
        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_MONEY_BY_NUM1", param);
    }

    /**
     * 对表TF_A_FEE_BALANCE操作
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static int insertFeeBalance(IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TI_B_SYNCHINFO", "INC_FEE_BALANCE", param);
    }

    // 添加押金记录
    public static int insUserForegift(IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_F_USER_FOREGIFT", "INS_USER", param);
    }

    /**
     * 插入台账
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static int regForMainTrade(IData inparams) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_B_TRADE", "INS_TRADE", inparams,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    /**
     * 插入台账
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static int regForMainOrder(IData inparams) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_B_ORDER", "INSERT_ORDER2", inparams,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 插入台账
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static int regForPayMoney(IData inparams) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_B_TRADEFEE_PAYMONEY", "INSERT_TRADEFEE_PAYMONEY", inparams,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 插入台账
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static int regForTradeFeesub(IData inparams) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_B_TRADEFEE_SUB", "INSERT_TRADEFEE_SUB", inparams,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 费用子台账查询
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset selTradeFeePayMoney(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        return Dao.qryByCode("TF_B_TRADEFEE_PAYMONEY", "SEL_BY_TRADE", param,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 费用子台账查询
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset selTradeFeesubByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        return Dao.qryByCode("TF_B_TRADEFEE_SUB", "SEL_ALL_BY_TRADE_WITHOUTGIFT", param,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 返销预存
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static int updataMoneyCancel(IData inparams) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_F_USER_FOREGIFT", "UPD_MONEY_CANCEL", inparams);
    }

    /**
     * 修改押金
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static int updataMoneyNum(IData inparams) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_F_USER_FOREGIFT", "UPD_MONEY", inparams);
    }

    /**
     * 押金转预存
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static int updataUserOtherServ(IData inparams) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_F_USER_OTHERSERV", "UPD_TAG_TIME_HAIN", inparams);
    }

    // 根据清退流水号trade_id返销用户其他资料表资料
    public static int updateOtherServ(IData param) throws Exception
    {

        return Dao.executeUpdateByCodeCode("TF_F_USER_OTHERSERV", "UPD_TAG_TIME_UNDO", param);
    }
}
