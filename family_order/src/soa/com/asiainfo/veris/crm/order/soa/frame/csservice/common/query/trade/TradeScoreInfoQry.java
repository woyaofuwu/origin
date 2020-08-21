
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeScoreInfoQry
{
    /**
     * 获取用户可以领取的礼品信息(包括已经领取的和尚未领取的)
     * 
     * @author huangsl
     * @date 2014-3-31
     * @param data
     * @return
     * @throws Exception
     * 改造：分库,duhj  2017/03/03
     * 方法一
     */
    public static IDataset getGiftOfStudyImbursement(String userId) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_USER_SI_SCORE_GIFT", inparams,Route.getJourDb(BizRoute.getRouteId()));//分库后查询jour用户
    }
    /**
     * 
     * @param userId
     * @param tradeId
     * @param ruleId
     * @return
     * @throws Exception
     * 改造：分库,duhj 2017/03/03
     * 方法二
     */
    public static IDataset getGifGoodsStates(String userId,String tradeId,String ruleId) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("TRADE_ID", tradeId);
        inparams.put("RULE_ID", ruleId);

        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_USER_SI_SCORE_GIFT2", inparams);//分库后查询crm
    }

    /**
     * 查询实名制赠送积分的台账信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getRealNameTradeScore(String userId) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_IS_REALNAMESCORE", params,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 获取可兑换充值卡信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getScoreExCardInfo(IData param) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_FOR_VALUECARD", param);
    }

    /**
     * 根据TRADE_ID获取所有台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAllIntegralacctByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_INTEGRALACCT", "SEL_BY_TRADE_ID", params,Route.getJourDb());
    }

    /**
     * 根据TRADE_ID获取所有积分计划变更历史
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAllIntegralplanByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_INTEGRALPLAN", "SEL_BY_TRADE_ID", params,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 根据TRADE_ID获取所有台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAllScoreRealtionByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_SCORERELATION", "SEL_BY_TRADE_ID", params,Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset getTradeScoreInfo(String tradeId, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("TRADE_EPARCHY_CODE", tradeTypeCode);
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_BY_TRADESCORE_RECEIPTNEW", param);
    }

    public static IDataset getTradeScoreInfoByUserIdTradeId(String tradeId, String suerId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("USER_ID", suerId);
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_BY_TRADEID_USERID", param,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 根据tradeid查询积分变更总值
     * 
     * @param trade_id
     * @return
     * @throws Exception
     */
    public static String qrySumScoreChanged(String trade_id) throws Exception
    {
        String sum = "0";
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("SCORE_TAG", "1");

        IDataset ids = Dao.qryByCodeParser("TF_B_TRADE_SCORE", "SEL_SUM_SCORE", param);

        if (IDataUtil.isNotEmpty(ids))
        {
            sum = ids.getData(0).getString("SUM");
        }

        return sum;
    }

    /**
     * 根据业务流水号，查询积分台帐
     * 
     * @param trade_id
     *            流水号
     * @param cancelTag
     *            0 有效
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeScoreInfos(String trade_id, String cancelTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("CANCEL_TAG", cancelTag);
        return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "SEL_BY_TRADE", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 根据tradeId和scoreTag查询积分台账信息
     * 
     * @param trade_id
     * @param scoreTag
     *            0 清理 1不清理
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeScoreInfosByTag(String trade_id, String scoreTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("SCORE_TAG", scoreTag);
        return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "SEL_BY_TRADE_TAG", param);
    }

    public static IDataset queryAdjustScore(IData data, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "SEL_QUERYSCOREADJUST", data, pagination,Route.getJourDb(BizRoute.getRouteId()));//订单库改为jour,duhj
    }

    public static IDataset queryAdjustScore(IData data, Pagination pagination, String eparchyCode) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "SEL_QUERYSCOREADJUST", data, pagination, Route.getJourDb(BizRoute.getRouteId()));//订单库改为jour,duhj);
    }

    public static IDataset queryAdjustScore_2(IData data, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "SEL_QUERYSCOREADJUST_2", data, pagination,Route.getJourDb(BizRoute.getRouteId()));//订单库改为jour,duhj
    }

    public static IDataset queryAdjustScore_2(IData data, Pagination pagination, String eparchyCode) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "SEL_QUERYSCOREADJUST_2", data, pagination, Route.getJourDb(BizRoute.getRouteId()));//订单库改为jour,duhj
    }

    public static IDataset queryHisScore(IData data, String eparchyCode) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "SEL_HIS_TRADESCORE", data, eparchyCode);
    }

    /**
     * @Function: queryTradeScoreByRsrvstr
     * @Description: 交易超时查询
     * @param: @param tradeTypeCode
     * @param: @param serialNumber
     * @param: @param orderId
     * @param: @param subscribeId
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 7:33:52 PM Jul 29, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Jul 29, 2013 longtian3 v1.0.0 TODO:
     */
    public static IDataset queryTradeScoreByRsrvstr(String tradeTypeCode, String serialNumber, String orderId, String subscribeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("ORDER_ID", orderId);
        param.put("SUBSCRIBE_ID", subscribeId);

        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_BY_RSRV_STR", param,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    /**
     * @Function: queryTradeScoreByRsrvstr
     * @Description: 交易超时查询
     * @param: @param tradeTypeCode
     * @param: @param serialNumber
     * @param: @param orderId
     * @param: @param subscribeId
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 7:33:52 PM Jul 29, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Jul 29, 2013 longtian3 v1.0.0 TODO:
     */
    public static IDataset queryTradeScoreByRsrvstrN(String tradeTypeCode, String serialNumber, String orderId, String subscribeId,String oprType) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("ORDER_ID", orderId);
        param.put("SUBSCRIBE_ID", subscribeId);
        param.put("RSRV_STR6", oprType);

        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_BY_RSRV_STRN", param,Route.getJourDb(BizRoute.getRouteId()));//modify by duhj 2017/03/13
    }

    public static IDataset queryTradeScoreInfo(IData data) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "SEL_TRADESCOREINFO", data);
    }

    /**
     * 根据tradeId查询积分台账：注：关联了兑换规则表
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeScoreJoinExchangeRule(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_BY_TRADEID", param,Route.getJourDb(BizRoute.getRouteId()));//修改为查询订单库jour,duhj 2017/03/07
    }

    public static IDataset getAllTradeBackInteAcctByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_INTEGRALACCT_BAK", "SEL_ALL_BAK_BY_TRADE", param);
    }

    public static IDataset getAllTradeBackIntePlanByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_INTEGRALPLAN_BAK", "SEL_ALL_BAK_BY_TRADE", param);
    }

    public static IDataset getAllTradeBackScoreRelaByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_SCORERELATION_BAK", "SEL_ALL_BAK_BY_TRADE", param);
    }
    
    public static IDataset getTradeScoreTradeBySn(String ruleId,String startTime,String endTime,String serialNumber,String flag) throws Exception
    {
        IData param = new DataMap();
        param.put("RULE_ID", ruleId);
        param.put("START_TIME", startTime);
        param.put("END_TIME", endTime);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("FLAG", flag);
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_TRADESCORE_BY_SN", param,Route.getJourDb(BizRoute.getRouteId()));//modify by duhj
    }
    /**
	 * 查询积分扣减订单信息
	 * @author songzy
	 * @param pd
	 * @param data
	 * @return tradeInfos
	 * @throws Exception
	 */
	public static IDataset queryPayOrRollbackInfo(IData params) throws Exception{
		return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "SEL_FOR_SCORE_PAYROLLBACK",params,Route.getJourDb(BizRoute.getRouteId()));
	}
	/**
	 * 查询需要冲正订单信息
	 * @author songzy
	 * @param pd
	 * @param data
	 * @return tradeInfos
	 * @throws Exception
	 */
	public static IDataset queryReviseInfo(IData params) throws Exception{
		return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "SEL_FOR_SCORE_REVISE",params,Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 查询交易记录信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static int queryTradeScoreRecode(IData param)throws Exception{
		IDataset set=Dao.qryByCodeParser("TL_B_TRADE_RECORD", "QRY_TRADE_RECORD_BY_RULE_ID",param);
		if(IDataUtil.isEmpty(set)){
			return 0;
		}else{
			return set.getData(0).getInt("TOTAL_NUMBER");
		}
	}
	
	/**
	 * 保存交易记录信息
	 * @param param
	 * @throws Exception
	 */
	public static void saveTradeScoreRecode(IData param)throws Exception{
		Dao.executeUpdateByCodeCode("TL_B_TRADE_RECORD", "INS_TRADE_RECORD",param);
	}
	
	/**
	 * 查询积分扣减订单信息
	 * @author songzy
	 * @param pd
	 * @param data
	 * @return tradeInfos
	 * @throws Exception
	 */
	public static IDataset queryDonateRollbackInfo(IData params) throws Exception{
		return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "SEL_FOR_SCORE_DONATEROLLBACK",params,Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static void insTradeScore(IData param) throws Exception{
		Dao.executeUpdateByCodeCode("TF_B_TRADE_SCORE", "INSERT_TRADE_SCORE_DATA",param,Route.getJourDb(BizRoute.getRouteId()));
	}

	
	/**
	 * REQ201702080017_关于积分业务的若干优化需求
	 * <br/>
	 * 只查询TF_B_TRADE_SCORE表
	 * 根据tradeId查询TradeScore信息
	 * @author zhuoyingzhi
	 * @date 20170308
	 * @param tradeId
	 * 合版本 duhj 2017/5/2
	 * @return
	 * @throws Exception
	 */
    public static IDataset queryTradeScoreByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        //TF_B_TRADE_SCORE  SEL_BY_TRADE_ID  在生产已经存在
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_BY_TRADE_ID", param,Route.getJourDb(BizRoute.getRouteId()));
    }
}
