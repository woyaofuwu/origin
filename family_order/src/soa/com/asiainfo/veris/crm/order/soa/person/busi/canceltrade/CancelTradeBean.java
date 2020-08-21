
package com.asiainfo.veris.crm.order.soa.person.busi.canceltrade;

import org.apache.log4j.Logger;

import com.ailk.biz.bean.BizBean;
import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ticket.TicketInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;
import com.asiainfo.veris.crm.order.soa.person.busi.einvoicehistory.EInvoiceHistoryBean;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CancelTradeBean.java
 * @Description: 业务返销bean
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-29 下午7:21:41
 */
public class CancelTradeBean extends CSBizBean
{
    private static transient final Logger logger = Logger.getLogger(CancelTradeBean.class);

    /**
     * 票据打印返销
     * 
     * @throws Exception
     */
    public void cancelNotePrintLog(IData hisTrade, IData pubData) throws Exception
    {
        if (hisTrade.getLong("OPER_FEE", 0) == 0L && hisTrade.getLong("ADVANCE_PAY", 0) == 0L)
            return;

        IData param = new DataMap();
        param.put("TRADE_ID", pubData.getString("TRADE_ID"));
        param.put("CANCEL_TIME", pubData.getString("SYS_TIME"));
        param.put("CANCEL_STAFF_ID", pubData.getString("STAFF_ID"));
        param.put("CANCEL_DEPART_ID", pubData.getString("DEPART_ID"));
        param.put("CANCEL_CITY_CODE", pubData.getString("CITY_CODE"));
        param.put("CANCEL_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        param.put("CANCEL_REASON_CODE", "");
        Dao.executeUpdateByCodeCode("TF_B_NOTEPRINTLOG", "UPD_CANCELTAG", param);
        // 宽带开户预存处理需求
        if (StringUtils.equals("600", hisTrade.getString("TRADE_TYPE_CODE", "0")))
        {
            IDataset tagInfos = TagInfoQry.getTagInfosByTagCode(pubData.getString("LOGIN_EPARCHY_CODE"), "WIDENET_PREFEE_TAG", "CSM", "0");
            if (IDataUtil.isEmpty(tagInfos))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_502);
            }
            String preFeeTagDate = tagInfos.getData(0).getString("TAG_INFO", "");
            String acceptDate = pubData.getString("SYS_TIME");
            if (acceptDate.compareTo(preFeeTagDate) > 0)
            {
                IData tempParam = new DataMap();
                tempParam.put("TRADE_ID", pubData.getString("TRADE_ID"));
                tempParam.put("FEE_MODE", "2");
                Dao.executeUpdateByCodeCode("TF_B_NOTEPRINTLOG", "CLEAR_CANCELTAG_BY_FEEMODE", tempParam);
            }
        }
    }

    /**
     * 校验是否能返销
     * 
     * @param hisTrade
     * @return
     * @throws Exception
     */
    private void checkCancelTrade(IData hisTrade) throws Exception
    {
        String userId = hisTrade.getString("USER_ID");
        String serialNumber = hisTrade.getString("SERIAL_NUMBER");
        String tradeTypeCode = hisTrade.getString("TRADE_TYPE_CODE");
        String tradeId = hisTrade.getString("TRADE_ID");
        if("141".equals(tradeTypeCode)){
        	return;
        }
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        if (ucaData == null)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        if (StringUtils.equals("10", tradeTypeCode))
        {
            // 统一付费校验，如号码为统一付费副号码，不予返销开户业务。
            IDataset tradeInfos = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56");
            if (IDataUtil.isNotEmpty(tradeInfos))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "该手机用户存在统一付费业务，不给予办理开户返销！");
            }
            // 判定用户是否存在亲亲网关系
            tradeInfos = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "45");
            if (IDataUtil.isNotEmpty(tradeInfos))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "该手机用户存在亲亲网关系，不给予办理开户返销！");
            }

            // 有宽带开户工单未完工，提示不给办理
            StringBuilder kdSerialNumber = new StringBuilder(50);
            kdSerialNumber.append("KD_").append(serialNumber);
            tradeInfos = TradeInfoQry.getWindTradeInfoBySn(kdSerialNumber.toString());
            if (IDataUtil.isNotEmpty(tradeInfos))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "该手机用户有宽带开户未完工工单，不给予办理开户返销！");
            }
            // 有宽带用户的手机用户不给办理业务
            IData kdUserInfo = UcaInfoQry.qryUserInfoBySn(kdSerialNumber.toString());           
            if (IDataUtil.isNotEmpty(kdUserInfo))
            {
                IData productInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
                if(IDataUtil.isNotEmpty(productInfo)){
                    IDataset productConfig = StaticUtil.getStaticList("NO_PHONE_PRODUCT");
                    if(IDataUtil.isNotEmpty(productConfig)){
                        String productId = productConfig.first().getString("DATA_ID", "20191209");
                        if(!productId.equals(productInfo.getString("PRODUCT_ID"))){
                            CSAppException.apperr(TradeException.CRM_TRADE_95, "该手机用户有有效的宽带用户，不给予办理开户返销！");
                        }
                    }
                }else{
                    CSAppException.apperr(TradeException.CRM_TRADE_95, "该手机用户有有效的宽带用户，不给予办理开户返销！");
                }
            }
        }
        
        IDataset dataset = CommparaInfoQry.getCommparaInfos("CNC", "1200", tradeTypeCode);
        if (IDataUtil.isNotEmpty(dataset))
        {
        	for(int i=0;i<dataset.size();i++)
        	{
	    		IData data = dataset.getData(i);
	    		String para_code1 = data.getString("PARA_CODE1", "");
	    		String para_code2 = data.getString("PARA_CODE2", "");
	    		String tab_name = data.getString("PARA_CODE3", "");
	    		String sql_ref = data.getString("PARA_CODE4", "");
	    		String msg = data.getString("PARA_CODE24", "不满足业务规则，不允许返销！");
	            IDataset codeinfos = CommparaInfoQry.getCodeCodeInfos(tab_name, sql_ref, para_code1, para_code2, tradeId);

	            if (!"0".equals(codeinfos.getData(0).getString("FLAG")))
	            {
	            	CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
	            }
        	}
            
        }
        	
        
    }
    
    /**
     * 电子发票返销
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void cancelEInvoice(IData hisTrade) throws Exception
    {
    	String tradeId = hisTrade.getString("TRADE_ID");
    	String accId = hisTrade.getString("ACCT_ID");
    	String userId = hisTrade.getString("USER_ID");
    	IDataset printPDFInfos = EInvoiceHistoryBean.queryPrintPDFLogByTradeId(tradeId);
    	if (IDataUtil.isNotEmpty(printPDFInfos))
        {
            for (int i = 0; i < printPDFInfos.size(); i++)
            {
            	//根据电子发票唯一表示调用账管接口冲红发票
            	String printId = printPDFInfos.getData(i).getString("PRINT_ID");
            	IData param = new DataMap();
            	param.put("TRADE_ID", tradeId);
                param.put("PRINT_ID", printId);
            	param.put("ACCT_ID", accId);
            	param.put("USER_ID", userId);
            	
            	EInvoiceHistoryBean.modifyEInvoice(param);
            } 
        }
    }
    

    /**
     * 返销前规则校验
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void chkTradeBeforeUndo(IData pubData, IData hisTrade) throws Exception
    {
        checkCancelTrade(hisTrade);// 业务规则校验
        // 执行配置规则
        IData inRuleParam = new DataMap();
        inRuleParam.put("TRADE_TYPE_CODE", hisTrade.getString("TRADE_TYPE_CODE"));
        inRuleParam.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        inRuleParam.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        inRuleParam.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        inRuleParam.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        inRuleParam.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        inRuleParam.put("TRADE_ID", pubData.getString("TRADE_ID"));
        inRuleParam.put("RULE_BIZ_TYPE_CODE", "TradeCheckBeforeUndo");
        inRuleParam.put("RULE_BIZ_KIND_CODE", "TradeCheckBeforeUndo");
        inRuleParam.put("ACTION_TYPE", "TradeCheckBeforeUndo");
        inRuleParam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inRuleParam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inRuleParam.put("UNDO_TIME", pubData.getString("SYS_TIME"));// 返销时间
        inRuleParam.put("TRADE_INFO", hisTrade);// 将历史trade信息传入
        IData data = BizRule.bre4SuperLimit(inRuleParam);
        CSAppException.breerr(data);
    }

    public String createCancelOrder(IData hisTradeData, IData pubData) throws Exception
    {
        IData newOrder = new DataMap();
        String newOrderId = SeqMgr.getOrderId();// 生成新的order_id
        newOrder.put("ORDER_ID", newOrderId);
        newOrder.put("ACCEPT_MONTH", newOrderId.substring(4, 6));
        newOrder.put("ORDER_TYPE_CODE", hisTradeData.getString("TRADE_TYPE_CODE"));
        newOrder.put("TRADE_TYPE_CODE", hisTradeData.getString("TRADE_TYPE_CODE"));
        newOrder.put("PRIORITY", hisTradeData.getString("PRIORITY"));
        newOrder.put("ORDER_STATE", "0");
        newOrder.put("NEXT_DEAL_TAG", "0");
        newOrder.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        String custId = hisTradeData.getString("CUST_ID");
        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (IDataUtil.isEmpty(custInfo)&&!"141".equals(hisTradeData.getString("TRADE_TYPE_CODE")))
        {
            CSAppException.apperr(CustException.CRM_CUST_105, hisTradeData.getString("SERIAL_NUMBER"));
        }
        newOrder.put("CUST_ID", custId);
        newOrder.put("CUST_NAME", hisTradeData.getString("CUST_NAME"));
        if("141".equals(hisTradeData.getString("TRADE_TYPE_CODE"))){
        newOrder.put("PSPT_TYPE_CODE", "");
        newOrder.put("PSPT_ID", "");
        }else{
        newOrder.put("PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE", ""));
        newOrder.put("PSPT_ID", custInfo.getString("PSPT_ID", ""));
        }
        newOrder.put("EPARCHY_CODE", hisTradeData.getString("EPARCHY_CODE"));
        newOrder.put("CITY_CODE", hisTradeData.getString("CITY_CODE"));

        newOrder.put("OPER_FEE", hisTradeData.getString("OPER_FEE"));
        newOrder.put("FOREGIFT", hisTradeData.getString("FOREGIFT"));
        newOrder.put("ADVANCE_PAY", hisTradeData.getString("ADVANCE_PAY"));
        newOrder.put("FEE_STATE", hisTradeData.getString("FEE_STATE"));

        newOrder.put("CANCEL_TAG", "2");
        newOrder.put("CANCEL_DATE", hisTradeData.getString("ACCEPT_DATE"));
        newOrder.put("CANCEL_STAFF_ID", hisTradeData.getString("TRADE_STAFF_ID"));
        newOrder.put("CANCEL_DEPART_ID", hisTradeData.getString("TRADE_DEPART_ID"));
        newOrder.put("CANCEL_CITY_CODE", hisTradeData.getString("TRADE_CITY_CODE"));
        newOrder.put("CANCEL_EPARCHY_CODE", hisTradeData.getString("TRADE_EPARCHY_CODE"));

        newOrder.put("EXEC_TIME", pubData.getString("SYS_TIME"));
        newOrder.put("FINISH_DATE", "");
        newOrder.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));

        newOrder.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        newOrder.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        newOrder.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        newOrder.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));

        newOrder.put("UPDATE_TIME", pubData.getString("SYS_TIME"));
        newOrder.put("UPDATE_STAFF_ID", pubData.getString("STAFF_ID"));
        newOrder.put("UPDATE_DEPART_ID", pubData.getString("DEPART_ID"));
        newOrder.put(Route.ROUTE_EPARCHY_CODE, pubData.getString("LOGIN_EPARCHY_CODE"));
        newOrder.put("SUBSCRIBE_TYPE", "0");// 默认写个0吧
        //Dao.insert("TF_B_ORDER", newOrder, Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
        Dao.insert("TF_B_ORDER", newOrder, Route.getJourDb(BizRoute.getRouteId()));//modify  by  duhj  2017/5/15 接口调试修正

        return newOrderId;
    }

    /**
     * 生成新订单数据
     * 
     * @pageData 页面传过来的数据；
     * @pubData 一些公共信息如：系统当前时间，工号、登陆地市等。
     * @throws Exception
     */
    public IData createCancelTrade(String newOrderId, IData hisTradeData, IData pageData, IData pubData) throws Exception
    {
        IData newTradeData = new DataMap();
        newTradeData.putAll(hisTradeData);

        /********* 费用 *******************************/
        long lOperFee = -hisTradeData.getLong("OPER_FEE", 0);
        long lAdvancePay = -hisTradeData.getLong("ADVANCE_PAY", 0);
        long lforegift = -hisTradeData.getLong("FOREGIFT", 0);
        String strFeeState = (lOperFee + lAdvancePay + lforegift == 0) ? "0" : "1";
        newTradeData.put("SUBSCRIBE_TYPE", hisTradeData.getString("SUBSCRIBE_TYPE"));
        newTradeData.put("OPER_FEE", String.valueOf(lOperFee));
        newTradeData.put("ADVANCE_PAY", String.valueOf(lAdvancePay));
        newTradeData.put("FOREGIFT", String.valueOf(lforegift));
        newTradeData.put("FEE_STATE", strFeeState);
        newTradeData.put("FEE_TIME", (strFeeState == "0") ? "" : pubData.getString("SYS_TIME"));
        newTradeData.put("FEE_STAFF_ID", (strFeeState == "0") ? "" : pubData.getString("STAFF_ID"));

        String subscribeType = hisTradeData.getString("SUBSCRIBE_TYPE");
        if (StringUtils.equals("97", subscribeType))
        {
            newTradeData.put("SUBSCRIBE_TYPE", "1");
        }

        newTradeData.put("ACCEPT_MONTH", newOrderId.substring(4, 6));
        
        newTradeData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
        newTradeData.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        newTradeData.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        newTradeData.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        newTradeData.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        newTradeData.put("EXEC_TIME", pubData.getString("SYS_TIME"));
        newTradeData.put("CANCEL_TAG", pubData.getString("CANCEL_TAG"));// 2=返销 3=取消
        newTradeData.put("CANCEL_DATE", hisTradeData.getString("ACCEPT_DATE"));
        newTradeData.put("CANCEL_STAFF_ID", hisTradeData.getString("TRADE_STAFF_ID"));
        newTradeData.put("CANCEL_DEPART_ID", hisTradeData.getString("TRADE_DEPART_ID"));
        newTradeData.put("CANCEL_CITY_CODE", hisTradeData.getString("TRADE_CITY_CODE"));
        newTradeData.put("CANCEL_EPARCHY_CODE", hisTradeData.getString("TRADE_EPARCHY_CODE"));
        // 以下字段取默认值
        newTradeData.put("BPM_ID", "");
        newTradeData.put("SUBSCRIBE_STATE", "0");
        newTradeData.put("NEXT_DEAL_TAG", "0");
        newTradeData.put("OLCOM_TAG", hisTradeData.getString("OLCOM_TAG"));
        newTradeData.put("FINISH_DATE", "");
        newTradeData.put("EXEC_ACTION", "");
        newTradeData.put("EXEC_RESULT", "");
        newTradeData.put("EXEC_DESC", "");

        newTradeData.put("UPDATE_TIME", pubData.getString("SYS_TIME"));
        newTradeData.put("UPDATE_STAFF_ID", pubData.getString("STAFF_ID"));
        newTradeData.put("UPDATE_DEPART_ID", pubData.getString("DEPART_ID"));
        newTradeData.put(Route.ROUTE_EPARCHY_CODE, pubData.getString("LOGIN_EPARCHY_CODE"));

        newTradeData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        newTradeData.put("TERM_IP", CSBizBean.getVisit().getRemoteAddr());
        if (StringUtils.isNotEmpty(pageData.getString("INVOICE_NO")))
        {
            newTradeData.put("INVOICE_NO", pageData.getString("INVOICE_NO"));
        }
        else
        {
            newTradeData.put("INVOICE_NO", hisTradeData.getString("INVOICE_NO", ""));
        }
        //REQ201611210014关于优化积分兑换和包电子券业务流程的需求-songxw-BOSS多只返销积分不返销电子券
        if (StringUtils.isNotEmpty(pageData.getString("UPAY_CANCEL_SCORE")))
        {
            newTradeData.put("UPAY_CANCEL_SCORE", pageData.getString("UPAY_CANCEL_SCORE"));
        }
        else
        {
            newTradeData.put("UPAY_CANCEL_SCORE", "0");
        }
        newTradeData.put("REMARK", pageData.getString("REMARKS", ""));

        // 待确认
        newTradeData.put("CHANNEL_TRADE_ID", "");
        newTradeData.put("CHANNEL_ACCEPT_TIME", "");
        newTradeData.put("CANCEL_TYPE_CODE", "");
        newTradeData.put("RSRV_TAG1", "");

        // 如果取出来的老trade没有pfwait值(老系统倒换过来的数据)，如果olcom_tag=1,则PFwait写为1，否则写为0，兼容一下
        if (StringUtils.isBlank(newTradeData.getString("PF_WAIT")))
        {
            if (StringUtils.equals("1", newTradeData.getString("OLCOM_TAG")))// 如果原单据是要发指令的
            {
                newTradeData.put("PF_WAIT", "1");
            }
            else
            {
                newTradeData.put("PF_WAIT", "0");
            }
        }

        String processTagSet = hisTradeData.getString("PROCESS_TAG_SET");

        String tradeTypeCode = hisTradeData.getString("TRADE_TYPE_CODE");

        String rsrvStr7 = hisTradeData.getString("RSRV_STR7");

        String agentTag = processTagSet.substring(3, 4);
        if (("1".equals(agentTag) || "700".equals(tradeTypeCode))
                && StringUtils.isNotEmpty(rsrvStr7))
        {
            //买断开户专用，处理费用
            newTradeData.put("RSRV_STR7", "-" + rsrvStr7);
        }

        newTradeData.put("ORDER_ID", newOrderId);// 新的订单号
        if (!Dao.insert("TF_B_TRADE", newTradeData, Route.getJourDb(BizRoute.getRouteId())))//modify  by  duhj  2017/5/15 接口调试修正
        {
            CSAppException.apperr(TradeException.CRM_TRADE_304, pubData.getString("TRADE_ID"));
        }

        return newTradeData;
    }

    /**
     * 生成省中心库返销订单
     * 
     * @throws Exception
     */
    public void createCenterUndoTrade(IData newCancelTrade, IData pubData) throws Exception
    {
        this.modifyCenterTrade(pubData);// 修改中心库原订单状态为被返销
        if (!Dao.insert("TF_B_TRADE", newCancelTrade, Route.CONN_CRM_CEN))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_306, pubData.getString("TRADE_ID"));
        }
    }

    /**
     * 修改中心库订单
     * 
     * @param tradeId
     * @throws Exception
     */
    private void modifyCenterTrade(IData pubData) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", pubData.getString("TRADE_ID"));
        param.put("CANCEL_TAG", "1");// 被返销或者取消
        param.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
        param.put("CANCEL_STAFF_ID", pubData.getString("STAFF_ID"));
        param.put("CANCEL_DEPART_ID", pubData.getString("DEPART_ID"));
        param.put("CANCEL_CITY_CODE", pubData.getString("CITY_CODE"));
        param.put("CANCEL_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_CANCEL_TAG", param, Route.CONN_CRM_CEN);
    }

    /**
     * 修改原历史订单
     * 
     * @param tradeId
     * @throws Exception
     */
    public void modifyHisTradeAndTradeStaff(IData hisTrade, IData pubData) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", pubData.getString("TRADE_ID"));
        param.put("CANCEL_TAG", "1");// 被返销
        param.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
        param.put("CANCEL_STAFF_ID", pubData.getString("STAFF_ID"));
        param.put("CANCEL_DEPART_ID", pubData.getString("DEPART_ID"));
        param.put("CANCEL_CITY_CODE", pubData.getString("CITY_CODE"));
        param.put("CANCEL_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        int cnt = Dao.executeUpdateByCodeCode("TF_BH_TRADE", "UPD_HISTRADE_CANCEL_TAG", param, Route.getJourDb(BizRoute.getRouteId()));//modify by duhj 接口调试修正
        if (cnt < 1)
        {
            CSAppException.apperr(TradeException.CRM_TRADE_305, pubData.getString("TRADE_ID"));
        }
    }

    /**
     * @methodName: cancelTicketTrade
     * @Description: 业务返销时作废原票据
     * @version: 
     * @author: 
     * @date: 
     */
    public void cancelTicketTrade(String tradeId) throws Exception{
    	IData param = new DataMap();
		IDataset ticketsInfo = TicketInfoQry.qryTradeTickets(tradeId);
		for (int i = 0, size = ticketsInfo.size(); i < size; ++i) {//对于当前流水中有效的票据进行作废处理
			// 调用票据作废服务
			param.put("PRINT_ID", ticketsInfo.getData(i).getString("PRINT_ID",""));
			param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode());
			CSAppCall.call("SS.ReceiptZFSVC.submitZFReceipt", param);
		}
	}

    public void createCancelStaffTrade(String newOrderId, IData pubData) throws Exception
    {
        String tradeId = pubData.getString("TRADE_ID");
        IDataset tradeStaffInfos = TradeStaffInfoQry.queryTradeStaffByTradeId(tradeId, "0");

        if (IDataUtil.isEmpty(tradeStaffInfos))
        {
            return;
        }

        // 修改原单据的状态
        IData param = new DataMap();

        param.put("TRADE_ID", tradeId);
        param.put("DAY", tradeStaffInfos.getData(0).getString("DAY"));
        param.put("CANCEL_TAG", "1");// 被返销
        param.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
        param.put("CANCEL_STAFF_ID", pubData.getString("STAFF_ID"));
        param.put("CANCEL_DEPART_ID", pubData.getString("DEPART_ID"));
        param.put("CANCEL_CITY_CODE", pubData.getString("CITY_CODE"));
        param.put("CANCEL_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        // 更新失败不报错
        Dao.executeUpdateByCodeCode("TF_BH_TRADE_STAFF", "UPD_STAFF_CANCEL_TAG", param, Route.getJourDb(BizRoute.getRouteId()));//modify by  duhj

        // 新增一条cancel_tag=2的单据
        IData newData = tradeStaffInfos.getData(0);

        if (newData.getLong("OPER_FEE", 0) > 0)
        {
            newData.put("OPER_FEE", (-1) * newData.getLong("OPER_FEE", 0));
        }

        if (newData.getLong("FOREGIFT", 0) > 0)
        {
            newData.put("FOREGIFT", (-1) * newData.getLong("FOREGIFT", 0));
        }

        if (newData.getLong("ADVANCE_PAY", 0) > 0)
        {
            newData.put("ADVANCE_PAY", (-1) * newData.getLong("ADVANCE_PAY", 0));
        }

        newData.put("ORDER_ID", newOrderId);
        newData.put("CANCEL_TAG", "2");
        newData.put("EXEC_TIME", pubData.getString("SYS_TIME"));

        // 新单据cancel字段记录的是原单据的 受理相关信息
        newData.put("CANCEL_DATE", newData.getString("ACCEPT_DATE"));
        newData.put("CANCEL_STAFF_ID", newData.getString("TRADE_STAFF_ID"));
        newData.put("CANCEL_DEPART_ID", newData.getString("TRADE_DEPART_ID"));
        newData.put("CANCEL_CITY_CODE", newData.getString("TRADE_CITY_CODE"));
        newData.put("CANCEL_EPARCHY_CODE", newData.getString("TRADE_EPARCHY_CODE"));
        newData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
        newData.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        newData.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        newData.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        newData.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));

        // 分区号
        newData.put("DAY", SysDateMgr.getSysDate("dd"));
        Dao.insert("TF_BH_TRADE_STAFF", newData, Route.getJourDb(BizRoute.getRouteId()));//modify by duhj
    }

    /**
     * 生成新的返销订单
     * 
     * @throws Exception
     */
    public void updateCancelOrder(IData pubData) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_INSTANCE_STATE", "0");
        param.put("ORDER_ID", pubData.getString("ORDER_ID"));
        Dao.executeUpdateByCodeCode("TF_BH_ORDER", "UPD_STATE_BY_ORDERID", param);
    }
    
    /**
     * @methodName: cancelTicketCheck
     * @Description: 业务返销时作废票据检查
     * @version: 
     * @author: 
     * @date: 
     */
    public IDataset cancelTicketCheck(IData param) throws Exception{
    	String tradeIdParam = param.getString("TRADE_ID");
        String tradeIdList = param.getString("TRADEID_LIST");
        if (StringUtils.isEmpty(tradeIdParam) && StringUtils.isEmpty(tradeIdList)){
            CSAppException.apperr(TradeException.CRM_TRADE_68);
        }
        String[] tradeIdArray = null;
        if (StringUtils.isNotEmpty(tradeIdList)){
            tradeIdArray = StringUtils.split(tradeIdList, ",");
        }else{
            tradeIdArray = StringUtils.split(tradeIdParam, ",");
        }
        IDataset tradeIdDataset = new DatasetList();
        for (int i = 0; i < tradeIdArray.length; i++){
            IData tempData = new DataMap();
            tempData.put("TRADE_ID", tradeIdArray[i]);
            tradeIdDataset.add(tempData);
        }
        DataHelper.sort(tradeIdDataset, "TRADE_ID", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        
    	StringBuilder sb = new StringBuilder();
    	for(int i=0,size=tradeIdDataset.size();i<size;++i){
    		IDataset ticketsInfo = TicketInfoQry.qryTradeTickets(tradeIdDataset.getData(i).getString("TRADE_ID",""));
    		for (int ticketsIndex = 0, ticketsCount = ticketsInfo.size(); ticketsIndex < ticketsCount; ++ticketsIndex) {//对于当前流水中有效的票据进行作废处理
    			sb.append("票据代码:");
    			sb.append(ticketsInfo.getData(ticketsIndex).getString("TAX_NO",""));
    			sb.append(" 票据号码:");
    			sb.append(ticketsInfo.getData(ticketsIndex).getString("TICKET_ID",""));
    			sb.append("\r\n");
    		}
    	}
		
    	IData rtParam = new DataMap();
    	rtParam.put("TICKET_INFO", sb.toString());
    	IDataset result = new DatasetList();
    	result.add(rtParam);
    	return result;
	}
    
    /**
     * 针对铁通宽带续费返销需要送服开数据的特殊处理  by songlm 20150522
     * 1、生成新的tf_b_order数据 2、生成tf_b_trade数据，使用部分历史数据，部分新数据 3、生成tf_b_trade_discnt数据，使用历史数据，其中优惠的天数改为负值
     */
    public String deal9000TradeTypeCancel(IData hisTradeData, IData pubData) throws Exception
    {
    	//1、生成tf_b_order数据
        IData newOrder = new DataMap();
        String newOrderId = SeqMgr.getOrderId();//生成新的order_id
        newOrder.put("ORDER_ID", newOrderId);
        newOrder.put("ACCEPT_MONTH", newOrderId.substring(4, 6));
        newOrder.put("ORDER_TYPE_CODE", "9000");
        newOrder.put("TRADE_TYPE_CODE", "9000");
        newOrder.put("PRIORITY", hisTradeData.getString("PRIORITY"));
        newOrder.put("ORDER_STATE", "0");
        newOrder.put("NEXT_DEAL_TAG", "0");
        newOrder.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        newOrder.put("CUST_ID", hisTradeData.getString("CUST_ID"));
        newOrder.put("CUST_NAME", hisTradeData.getString("CUST_NAME"));
        newOrder.put("PSPT_TYPE_CODE", hisTradeData.getString("PSPT_TYPE_CODE", ""));
        newOrder.put("PSPT_ID", hisTradeData.getString("PSPT_ID", ""));
        newOrder.put("EPARCHY_CODE", hisTradeData.getString("EPARCHY_CODE"));
        newOrder.put("CITY_CODE", hisTradeData.getString("CITY_CODE"));
        newOrder.put("ACCEPT_DATE", pubData.getString("ACCEPT_DATE"));
        newOrder.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        newOrder.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        newOrder.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        newOrder.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        newOrder.put("OPER_FEE", "0");//特殊工单无需费用
        newOrder.put("FOREGIFT", "0");//特殊工单无需费用
        newOrder.put("ADVANCE_PAY", "0");//特殊工单无需费用
        newOrder.put("FEE_STATE", "0");//特殊工单无需费用
        newOrder.put("EXEC_TIME", pubData.getString("SYS_TIME"));
        newOrder.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
        newOrder.put("CANCEL_TAG", "0");//注意修改为0，不能为2
        newOrder.put("UPDATE_TIME", pubData.getString("SYS_TIME"));
        newOrder.put("UPDATE_STAFF_ID", pubData.getString("STAFF_ID"));
        newOrder.put("UPDATE_DEPART_ID", pubData.getString("DEPART_ID"));
        newOrder.put(Route.ROUTE_EPARCHY_CODE, pubData.getString("LOGIN_EPARCHY_CODE"));
        newOrder.put("SUBSCRIBE_TYPE", "0");
 
        if (!Dao.insert("TF_B_ORDER", newOrder))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "宽带续费返销业务送服开数据插tf_f_order表失败！");
        }

        //2、生成tf_b_trade数据
        IData newTradeData = new DataMap();
        newTradeData.putAll(hisTradeData);//先使用历史台帐数据，之后再把需要更新的重新put
        String newTradeId = SeqMgr.getTradeIdFromDb();//生成新的trade_id
        
        newTradeData.put("TRADE_ID", newTradeId);
        newTradeData.put("ACCEPT_MONTH", newOrderId.substring(4, 6));
        newTradeData.put("ORDER_ID", newOrderId);
        newTradeData.put("BPM_ID", "");
        newTradeData.put("TRADE_TYPE_CODE", "9000");
        newTradeData.put("SUBSCRIBE_TYPE", "0");
        newTradeData.put("SUBSCRIBE_STATE", "0");
        newTradeData.put("NEXT_DEAL_TAG", "0");
        newTradeData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        newTradeData.put("INTF_ID", "TF_B_TRADE_DISCNT");//
        newTradeData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
        newTradeData.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        newTradeData.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        newTradeData.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        newTradeData.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        newTradeData.put("TERM_IP", CSBizBean.getVisit().getRemoteAddr());
        newTradeData.put("OPER_FEE", "0");
        newTradeData.put("FOREGIFT", "0");
        newTradeData.put("ADVANCE_PAY", "0");
        newTradeData.put("INVOICE_NO", "");
        newTradeData.put("FEE_STATE", "0");
        newTradeData.put("FEE_TIME", "");
        newTradeData.put("FEE_STAFF_ID", "");
        newTradeData.put("OLCOM_TAG", "1");//发指令
        newTradeData.put("FINISH_DATE", "");
        newTradeData.put("EXEC_TIME", pubData.getString("SYS_TIME"));
        newTradeData.put("EXEC_ACTION", "");
        newTradeData.put("EXEC_RESULT", "");
        newTradeData.put("EXEC_DESC", "");
        newTradeData.put("CANCEL_TAG", "0");
        newTradeData.put("UPDATE_TIME", pubData.getString("SYS_TIME"));
        newTradeData.put("UPDATE_STAFF_ID", pubData.getString("STAFF_ID"));
        newTradeData.put("UPDATE_DEPART_ID", pubData.getString("DEPART_ID"));
        newTradeData.put(Route.ROUTE_EPARCHY_CODE, pubData.getString("LOGIN_EPARCHY_CODE"));
        newTradeData.put("REMARK", "铁通宽带续费返销代码中插入的该工单数据");
        newTradeData.put("PF_WAIT", "1");//等服务开通回单，即SUBSCRIBE_STATE：F
  
        if (!Dao.insert("TF_B_TRADE", newTradeData))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "宽带续费返销业务送服开数据插tf_f_trade表失败！");
        }

        //3、生成tf_b_discnt数据
        IData newDiscntTradeData = new DataMap();
        
        String hisTradeId = hisTradeData.getString("TRADE_ID");//获取历史trade_id
        IDataset discntHisTradeInfos = TradeDiscntInfoQry.getTradeDiscntByTradeId(hisTradeId);//根据历史trade_id获取到tf_b_trade_discnt数据
        if(IDataUtil.isEmpty(discntHisTradeInfos))
        {
        	CSAppException.apperr(TradeException.CRM_TRADE_40);
        }
        IData discntHisTradeInfo = discntHisTradeInfos.getData(0);
        
        newDiscntTradeData.putAll(discntHisTradeInfo);//使用原discnt台帐数据，之后再把需要更新的重新put
        newDiscntTradeData.put("TRADE_ID", newTradeId);
        newDiscntTradeData.put("START_DATE", discntHisTradeInfo.getString("END_DATE"));
        newDiscntTradeData.put("END_DATE", discntHisTradeInfo.getString("START_DATE"));
        newDiscntTradeData.put("UPDATE_TIME", pubData.getString("SYS_TIME"));
        newDiscntTradeData.put("UPDATE_STAFF_ID", pubData.getString("STAFF_ID"));
        newDiscntTradeData.put("UPDATE_DEPART_ID", pubData.getString("DEPART_ID"));
        newDiscntTradeData.put("RSRV_NUM1", "-"+discntHisTradeInfo.getString("RSRV_NUM1"));
        
        if (!Dao.insert("TF_B_TRADE_DISCNT", newDiscntTradeData))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "宽带续费返销业务送服开数据插tf_b_trade_discnt表失败！");
        }
    
        return newOrderId;
    }
}
