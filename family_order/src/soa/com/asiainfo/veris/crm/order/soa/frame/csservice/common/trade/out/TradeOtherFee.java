
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeOtherFeeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.sync.TradeSyncFeeSub;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;

public class TradeOtherFee
{
    private final static Logger logger = Logger.getLogger(TradeOtherFee.class);

    public static void finishOtherFee(IData mainTrade) throws Exception
    {
        String intfId = mainTrade.getString("INTF_ID", "");

        if (StringUtils.isNotBlank(intfId) && intfId.indexOf("TF_B_TRADEFEE_OTHERFEE,") == -1)
        {
            return;
        }

        String tradeId = mainTrade.getString("TRADE_ID");

        // 取费用台账
        IDataset otherFeeList = TradeOtherFeeInfoQry.getTradeOtherFeeByTradeId(tradeId);

        if (IDataUtil.isEmpty(otherFeeList))
        {
            return;
        }

        // 根据费用类型做不同处理
        for (int i = 0; i < otherFeeList.size(); i++)
        {
            IData tradeOtherFee = otherFeeList.getData(i);

            // 费用类型
            String operType = tradeOtherFee.getString("OPER_TYPE");

            if (BofConst.OTHERFEE_FMY_TRANS.equals(operType))
            {
                tradeFmyAcctTrans(mainTrade, tradeOtherFee);
            }
            else if (BofConst.OTHERFEE_AGENT.equals(operType))
            {
                tradeAgentFee(mainTrade, tradeOtherFee);
            }
            /*
             * 营销活动截止调用账务接口转账转移 else if (BofConst.OTHERFEE_END_ACTIVE.equals(operType)) { endActiveFee(mainTrade,
             * tradeOtherFee); }
             */
            else if(BofConst.OTHERFEE_ROAMFEE_TRANS.equals(operType)){
            	tradeRoamFee(mainTrade, tradeOtherFee);
            }
        }
    }
    
    private static void tradeRoamFee(IData mainTrade, IData tradeOtherFee) throws Exception
    {
    	 
    	String tradeId = mainTrade.getString("TRADE_ID");
        IDataset tradeAttr = TradeAttrInfoQry.getTradeAttrByTradeIDandAttrCode(tradeId, "PROD_INST_ID", null);
        logger.debug("TradeOtherFee--tradeRoamFee--tradeAttr="+tradeAttr); 
        
        IData param = new DataMap();
        String operType = tradeOtherFee.getString("RSRV_STR1");//1:订购；2:激活；3:退订；4: 未激活，已过期
        
        param.put("ACCT_ID", tradeOtherFee.getString("ACCT_ID"));
        param.put("ACCESS_NO", mainTrade.getString("SERIAL_NUMBER"));
        param.put("OPER_TYPE", operType);
        param.put("FEE",  tradeOtherFee.getString("OPER_FEE"));
        
        //受理时间格式转换
        String acceptDate = mainTrade.getString("ACCEPT_DATE");
        Timestamp birthTimestamp = SysDateMgr.encodeTimestamp(acceptDate);
        acceptDate = DateFormatUtils.format(birthTimestamp.getTime(), "yyyyMMddHHmmss");
        param.put("TRADE_DATE", acceptDate);
        
        String businessIdA = "";
        String businessIdB = "";
        if("1".equals(operType))
        {//订购时使用订购流水号
        	businessIdA = tradeAttr.getData(0).getString("ATTR_VALUE");
        }
        else
        {//其他操作使用随机序列用于区分
        	businessIdA = tradeId;
        	businessIdB = tradeAttr.getData(0).getString("ATTR_VALUE");
        }
        
        logger.debug("TradeOtherFee--tradeRoamFee--detailItemCode="+tradeOtherFee.getString("RSRV_STR2",""));
        param.put("PEER_BUSINESS_ID_A",  businessIdA);
        param.put("PEER_BUSINESS_ID_B",  businessIdB);
        param.put("BILL_ITEM",tradeOtherFee.getString("RSRV_STR2",""));//账目编码

        IData result = new DataMap();
        String flag = "0000"; // 调账管接口 0000-成功，2998-异常
        tradeOtherFee.put("RSRV_STR6", flag);
        tradeOtherFee.put("RSRV_STR7", "调账管 AM_CRM_DoRomanAccep 接口返回成功！");
        try{
        	// 调账管 AM_CRM_DoRomanAccep 接口
    		result = AcctCall.doRomanAccep(param);
    		if(!"0000".equals(result.getString("RESULT_CODE"))){
    			flag = result.getString("RESULT_CODE");
    			tradeOtherFee.put("RSRV_STR6", flag);//
    	        tradeOtherFee.put("RSRV_STR7", result.getString("RESULT_MSG"));
    		}
        }catch(Exception e){
        	logger.debug("TradeOtherFee--tradeRoamFee--AcctCall.doRomanAccep exception-:"+e.getMessage());
        	// 执行异常 置为 2998 
        	flag = "2998";
        	String message ="";
        	if (e.getMessage().length()>=200) {
        		message = e.getMessage().substring(0, 200);
			}else {
				message = e.getMessage();
			}
        	tradeOtherFee.put("RSRV_STR6", flag);
            tradeOtherFee.put("RSRV_STR7", message);
        }
		tradeOtherFee.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		// 保存 调用成功与否状态入表
		Dao.update("TF_B_TRADEFEE_OTHERFEE", tradeOtherFee,new String[]{"TRADE_ID","OPER_TYPE"},Route.getJourDb(BizRoute.getRouteId()));
		
    }

    /**
     * 插费用同步表
     * 
     * @param feeDataset
     * @param tradeId
     * @param syncId
     * @throws Exception
     */
    private static IDataset getTransFee(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String syncId = mainTrade.getString("SYNC_SEQUENCE");

        // 获取不同账户转账子台账
        IDataset tradeTransFees = TradeOtherFeeInfoQry.getTradeOtherFeeByPK(tradeId, BofConst.OTHERFEE_DIFF_TRANS);
        if (IDataUtil.isEmpty(tradeTransFees))
        {
            return tradeTransFees;
        }

        IDataset syncRecvDataset = new DatasetList();

        for (int i = 0; i < tradeTransFees.size(); i++)
        {
            IData tradeTransFee = tradeTransFees.getData(i);
            String userId = tradeTransFee.getString("USER_ID");
            String acctId = tradeTransFee.getString("ACCT_ID");
            String userId2 = tradeTransFee.getString("USER_ID2");
            String acctId2 = tradeTransFee.getString("ACCT_ID2");

            if (StringUtils.isBlank(userId2) || StringUtils.isBlank(acctId2))
            {
                CSAppException.apperr(TradeOtherFeeException.CRM_TRADEOTHERFEE_2, tradeId);
            }

            /*
             * 帐务约定： payment_op 储值操作类型 所有得缴费 传 16000 含义 储值 所有得退款 传 16001 含义 清退 所有得返销 传 16003 含义 返销 16004 转出 16005 转入
             * 16002 调减 channel_id 渠道标识 所有都传 15000 含义 营业厅营业 PAYMENT_ID 储值方式 （可配置）
             */

            // 费用同步公用数据准备 - start
            IData tradeTransFeeData = new DataMap();
            tradeTransFeeData.put("SYNC_SEQUENCE", syncId);
            tradeTransFeeData.put("SYNC_DAY", StrUtil.getAcceptDayById(syncId));
            tradeTransFeeData.put("OUTER_TRADE_ID", tradeId);
            tradeTransFeeData.put("TRADE_ID", SeqMgr.getChargeId());
            tradeTransFeeData.put("BATCH_ID", tradeId);
            tradeTransFeeData.put("RECV_TIME", SysDateMgr.getSysTime());
            tradeTransFeeData.put("USER_ID", userId);
            tradeTransFeeData.put("ACCT_ID", acctId);
            tradeTransFeeData.put("USER_ID2", userId2);
            tradeTransFeeData.put("ACCT_ID2", acctId2);
            tradeTransFeeData.put("RECV_EPARCHY_CODE", mainTrade.getString("TRADE_EPARCHY_CODE"));
            tradeTransFeeData.put("RECV_CITY_CODE", mainTrade.getString("TRADE_CITY_CODE"));
            tradeTransFeeData.put("RECV_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID"));
            tradeTransFeeData.put("RECV_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
            tradeTransFeeData.put("CHANNEL_ID", "15000");
            tradeTransFeeData.put("PAYMENT_ID", "0");
            tradeTransFeeData.put("PAYMENT_OP", "16004");
            tradeTransFeeData.put("WRITEOFF_MODE", "1");
            tradeTransFeeData.put("PRIORITY", "0");
            tradeTransFeeData.put("PAYMENT_REASON_CODE", "0");
            tradeTransFeeData.put("PAY_FEE_MODE_CODE", "0");
            tradeTransFeeData.put("CANCEL_TAG", "0");
            tradeTransFeeData.put("DEAL_TAG", "0");
            tradeTransFeeData.put("ACT_TAG", "4");
            tradeTransFeeData.put("RESULT_CODE", "0");
            tradeTransFeeData.put("MONTHS", "240");
            tradeTransFeeData.put("AMONTH", "0");
            tradeTransFeeData.put("LIMIT_MONEY", "-1");
            tradeTransFeeData.put("MODIFY_TAG", "0");
            tradeTransFeeData.put("VALID_TAG", "0");
            tradeTransFeeData.put("ACTION_CODE", "-1"); // 默认，后续修改
            tradeTransFeeData.put("START_CYCLE_ID", "190001"); // 原账务逻辑写死
            tradeTransFeeData.put("END_CYCLE_ID", "205001"); // 原账务逻辑写
            tradeTransFeeData.put("RSRV_DATE1", SysDateMgr.getSysTime());

            tradeTransFeeData.put("TRADE_TYPE_CODE", "7043");
            tradeTransFeeData.put("RECV_FEE", "99999999");// 过户转帐取最大值(全转)
            syncRecvDataset.add(tradeTransFeeData);
            // 费用同步数据准备 - end
        }
        return syncRecvDataset;
    }

    public static int syncTransFee(IData mainTrade) throws Exception
    {
        String intfId = mainTrade.getString("INTF_ID", "");

        if (StringUtils.isNotBlank(intfId) && intfId.indexOf("TF_B_TRADEFEE_OTHERFEE,") == -1)
        {
            return 0;
        }

        // 费用同步表数据准备
        IDataset transFee = getTransFee(mainTrade);

        // 插费用同步表
        if (IDataUtil.isEmpty(transFee))
        {
            return 0;
        }
        TradeSyncFeeSub.insSyncRecv(transFee);

        return transFee.size();
    }

    private static void tradeAgentFee(IData mainTrade, IData tradeAgentFee) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");

        // 获取值
        String cancel_tag = mainTrade.getString("CANCEL_TAG");
        String serial_number = mainTrade.getString("SERIAL_NUMBER");
        String rsrvStr3 = mainTrade.getString("RSRV_STR3");
        String accept_date = mainTrade.getString("ACCEPT_DATE");
        String finish_date = mainTrade.getString("FINISH_DATE");

        IData agentData = new DataMap();

        agentData.put("TRADE_EPARCHY_CODE", mainTrade.getString("TRADE_EPARCHY_CODE"));
        agentData.put("TRADE_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID"));
        agentData.put("TRADE_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
        agentData.put("USER_ID", tradeAgentFee.getString("USER_ID"));
        agentData.put("RSRV_STR3", "1");
        agentData.put("X_FPAY_FEE", tradeAgentFee.getString("OPER_FEE"));
        agentData.put("EPARCHY_CODE", mainTrade.getString("TRADE_EPARCHY_CODE"));
        agentData.put("SERIAL_NUMBER", serial_number);

        if ("0".equals(cancel_tag))
        {
            agentData.put("OPER_TYPE_CODE", "1");
        }
        else if ("2".equals(cancel_tag))
        {
            // 返销
            agentData.put("OPER_TYPE_CODE", "2");
            agentData.put("X_ORIGIN_CHARGE_ID", tradeAgentFee.getString("REQUEST_ID", rsrvStr3));
            agentData.put("CHARGE_ID", tradeId);
            agentData.put("CUST_TYPE", "0");
            agentData.put("START_DATE", accept_date);
            agentData.put("END_DATE", finish_date);
        }

        IDataset dataset = CSAppCall.call("CHL_ChnlSkyIn", agentData);

        String charge_id = dataset.getData(0).getString("CHARGE_ID");
        String info_type = dataset.getData(0).getString("INFO_TYPE", "");
        String info_content = dataset.getData(0).getString("INFO_CONTENT");
        if (!"1".equals(info_type))
        {
            CSAppException.apperr(BizException.CRM_BIZ_5, info_content);
        }
        if (StringUtils.isNotBlank(charge_id))
        {
            TradeOtherFeeInfoQry.updateTradeOtherFeeByPK(tradeId, BofConst.OTHERFEE_AGENT, charge_id);
        }
    }

    private static void tradeFmyAcctTrans(IData mainTrade, IData tradeOtherFee) throws Exception
    {
        String cancel_tag = mainTrade.getString("CANCEL_TAG");

        if (!"0".equals(cancel_tag))
        {
            return;
        }

        String userId = tradeOtherFee.getString("USER_ID");
        String acctId = tradeOtherFee.getString("ACCT_ID");
        String userId2 = tradeOtherFee.getString("USER_ID2");
        String acctId2 = tradeOtherFee.getString("ACCT_ID2");

        IData params = new DataMap();

        params.put("USER_ID", userId);// 支付用户ID 查三户使用
        params.put("USER_ID_A", userId);// 支付用户ID
        params.put("ACCT_ID_A", acctId);// 支付账户ID
        params.put("USER_ID_B", userId2);// 被支付用户ID
        params.put("ACCT_ID_B", acctId2);// 被支付用户ID
        params.put("IN_TAG", "0");// 0：主卡销号、解散家庭成员关系时使用

        CSAppCall.callAcct("TAM_CRM_FamilAcctTrans", params, false);
    }

    /**
     * 营销活动截止调用账务接口
     * 
     * @param mainTrade
     * @param tradeOtherFee
     * @throws Exception
     */
    /*
     * private static void endActiveFee(IData mainTrade, IData tradeOtherFee) throws Exception{ // TODO Auto-generated
     * method stub String cancel_tag = mainTrade.getString("CANCEL_TAG"); if (!"0".equals(cancel_tag)) { return; }
     * String userId = tradeOtherFee.getString("USER_ID"); String acctId = tradeOtherFee.getString("ACCT_ID"); String
     * userId2 = tradeOtherFee.getString("USER_ID2"); String acctId2 = tradeOtherFee.getString("ACCT_ID2"); String
     * tradeId = tradeOtherFee.getString("RSRV_STR1",""); String discntCode = tradeOtherFee.getString("RSRV_STR2","");
     * IData params = new DataMap(); params.put("USER_ID", userId);// 支付用户ID 查三户使用 params.put("SERIAL_NUMBER",
     * mainTrade.getString("SERIAL_NUMBER")); params.put("TRADE_ID", tradeId);// 交易流水 params.put("FEEPOLICY_ID",
     * discntCode);// 资费编码 params.put("TRANS_TAG", "0");// 0 预存，1 赠送 账务侧重新取 params.put("CHANNEL_ID", "15000");//
     * if(StringUtils.isNotBlank(tradeId) && StringUtils.isNotBlank(discntCode)){
     * CSAppCall.callAcct("TAM_CRM_StopDiscntAction", params, false); } }
     */
}
