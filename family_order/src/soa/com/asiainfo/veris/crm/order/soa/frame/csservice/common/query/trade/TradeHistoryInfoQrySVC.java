
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;

public class TradeHistoryInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 获取用户指定业务最近一次办理记录
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-14
     */
    public static IData getTradeInfoByUserTrade(IData input) throws Exception
    {
        return TradeHistoryInfoQry.getTradeInfoByUserTrade(input.getString("USER_ID"), input.getString("TRADE_TYPE_CODE"));
    }

    /**
     * 获取能够返销的订单数据
     * 
     * @param serialNumber
     * @param tradeTypeCode
     * @param tradeEaprchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryCanBackTradeBySnAndTypeCode(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        String tradeEaprchyCode = input.getString("QRY_TRADE_EPARCHY_CODE");// 不能使用TRADE_EPARCHY_CODE 参数了
        return TradeHistoryInfoQry.queryCanBackTradeBySnAndTypeCode(userId, tradeTypeCode, tradeEaprchyCode);
    }

    /**
     * 获取能够指定时间段内可返销的订单数据
     * 
     * @param serialNumber
     * @param tradeTypeCode
     * @param beginDate
     * @param endDate
     * @param tradeEaprchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryCanBackTradeBySnAndTypeCodeAndDate(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        String tradeEaprchyCode = input.getString("QRY_TRADE_EPARCHY_CODE");// 不能使用TRADE_EPARCHY_CODE 参数了
        if (StringUtils.isEmpty(tradeEaprchyCode))
        {
            tradeEaprchyCode = CSBizService.getVisit().getStaffEparchyCode();
        }
        String beginDate = input.getString("START_DATE");
        String endDate = input.getString("END_DATE");
        return TradeHistoryInfoQry.queryCanBackTradeBySnAndTypeCodeAndDate(userId, tradeTypeCode, tradeEaprchyCode, beginDate, endDate);
    }

    /**
     * 根据trade_id,cancel_tag查询历史台账
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeHistoryInfos(IData input) throws Exception
    {

        IDataset ds = new DatasetList();
        String tradeId = input.getString("TRADE_ID");
        String cancelTag = input.getString("CANCEL_TAG");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE", "").trim();

        if (tradeTypeCode.equals("230")) {//预约营销活动受理 
            ds = TradeHistoryInfoQry.query_TF_B_TRADE_SALEACTIVE_BOOK_ByTradeId(tradeId);
        } else if (tradeTypeCode.equals("240")) {//营销活动取消237、营销活动受理
            ds = TradeHistoryInfoQry.query_TF_B_TRADE_SALE_ACTIVE_ByTradeId1(tradeId);
        } else if (tradeTypeCode.equals("330") || tradeTypeCode.equals("3301")) {//积分兑换、 积分兑换发E拇指
            ds = TradeHistoryInfoQry.query_TF_B_TRADE_SALE_ACTIVE_ByTradeId(tradeId);
            if (!ds.isEmpty()) {
//                String userId = ds.getData(0).getString("USER_ID", "");
//                // 查用户积分
//                IDataset scoreInfo = AcctCall.queryUserScore(userId);
//                String score = "0";
//                if (IDataUtil.isNotEmpty(scoreInfo)) {
//                    score = scoreInfo.getData(0).getString("SUM_SCORE"); // 用户可兑换积分
//                }

                for (int i = 0; i < ds.size(); i++) {
                    IData data = ds.getData(i);
                    //data.put("SCORE", score);// 替换为 用户可兑换积分

                    data.put("SCORE_CHANGED", data.getInt("SCORE_CHANGED", 0) * -1); // 所扣减的积分是在库里保存的是负值，需转换为正数。

                    // RSRV_STR1 保存的是 兑换类型编码
                    String exchangeType = TradeHistoryInfoQry.query_EXCHANGE_TYPE(data.getString("RSRV_STR1")).getData(0).getString("EXCHANGE_TYPE", "");
                    data.put("EXCHANGE_TYPE", exchangeType);
                }
            }

        } else if (tradeTypeCode.equals("142")) {//补换卡

            ds = TradeHistoryInfoQry.query_TF_B_TRADE_ByTradeId(tradeId);
            IData data = ds.getData(0);
            //对旧sim卡类型进行翻译
            String netTypeCode = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER")).getString("NET_TYPE_CODE");
            IDataset simInfos = ResCall.getSimCardInfo("0", data.getString("RSRV_STR9"), "", "", netTypeCode);
            if (!simInfos.isEmpty()) {
                IData simcard = simInfos.getData(0);
                String resKindName = simcard.getString("RES_TYPE_NAME", "");
                if (StringUtils.isBlank(resKindName)) {
                    resKindName = StaticUtil.getStaticValueDataSource(getVisit(), Route.CONN_RES, "RES_TYPE", "RES_TYPE_ID", "RES_TYPE_NAME", simcard.getString("RES_TYPE_CODE", ""));
                }
                data.put("RES_TYPE_NAME", resKindName);
            }
        } else {
            ds = IDataUtil.idToIds(UTradeHisInfoQry.qryTradeHisByPk(tradeId, cancelTag, null));
        }

        return ds;
    }

    /**
     * MAS费用返销的权限控制，data_code 为GROUP998，right_class 取值说明： 1 隔笔返销 2 当日返销域权 3 隔日返销域权 4 隔月 T 验证权限，不受时间限制 查询集团可返销业务
     * 
     * @author fuzn
     * @date 2013-04-01
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeInfosWithPopedom(IData input) throws Exception
    {
        return TradeHistoryInfoQry.queryTradeInfosWithPopedom(input.getString("provGrpCancelFee", ""), input.getString("CUST_ID", ""), input.getString("SERIAL_NUMBER", ""), input.getString("CANCEL_TAG", ""), null);
    }

    /**
     * 根据客户ID查询历史台帐信息
     * 
     * @throws Exception
     */
    public static IDataset queryTradesByCustId(IData input) throws Exception
    {
        return TradeHistoryInfoQry.queryTradesByCustId(input.getString("CUST_ID", ""), input.getString("CANCEL_STAFF_ID", ""), null);
    }

    /**
     *  查指定用户和业务的办理历史记录
     * 
     * @throws Exception
     */
    public static IDataset getInfosByUserIdTradeTypeCodeFor3037(IData input) throws Exception
    {
    	 String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
         String mebUserId = input.getString("USER_ID");
         String startDate = DiversifyAcctUtil.getFirstTimeThisAcct(mebUserId);
         String endDate = DiversifyAcctUtil.getLastTimeThisAcctday(mebUserId, null);
         
        return TradeHistoryInfoQry.getInfosByUserIdTradeTypeCode(tradeTypeCode, mebUserId, startDate, endDate);
    }
    
}
