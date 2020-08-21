package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class BbossSendMsg implements ITradeFinishAction
{
	@Override
	public void executeAction(IData mainTrade) throws Exception
	{
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");
		String tradeId = mainTrade.getString("TRADE_ID");
		
		IDataset merchpTrades = TradeGrpMerchpInfoQry.qryMerchpInfoByTradeId(tradeId);
		IDataset trades = TradeInfoQry.getMainTradeByTradeIdForGrp(tradeId);
		if (IDataUtil.isNotEmpty(merchpTrades) && IDataUtil.isNotEmpty(trades)) {
			String custId = trades.getData(0).getString("CUST_ID");
			//获取产品名称
			IData merchpTrade = merchpTrades.getData(0);
			String modifyTag = merchpTrade.getString("MODIFY_TAG");
			String statuStr = "";
			String qwID = merchpTrade.getString("PRODUCT_SPEC_CODE");///-----------需要补上字段
			// 获取全网集团编码转换本地产品编码
			String offerCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
			        { "ID", "ID_TYPE", "ATTR_VALUE", "ATTR_OBJ" }, "ATTR_CODE", new String[]
			        { "1", "B", qwID, "PRO" });
			
			IData offer = UpcCall.queryOfferByOfferId("P",offerCode);
			if (IDataUtil.isEmpty(offer)){
				CSAppException.appError("-1", "找不到对应产品信息，全网编码："+qwID+"，本地编码:"+offerCode);
			}
			String offerName = offer.getString("OFFER_NAME");
			
			//跳过IBOSS000工号不处理
			String staffId = merchpTrade.getString("UPDATE_STAFF_ID");
			if (staffId == null || "".equals(staffId) || "IBOSS000".equals(staffId)) {
				return;
			}
			
			
			//获取办理人姓名
			String staffName = UStaffInfoQry.getStaffNameByStaffId(merchpTrade.getString("UPDATE_STAFF_ID"));
			String phone = UStaffInfoQry.getStaffSnByStaffId(merchpTrade.getString("UPDATE_STAFF_ID"));
			
			//获取集团名称
			IDataset custGrps = GrpInfoQry.queryGrpIdBygrpcustId(custId);
			
			//获取状态
			if ("0".equals(modifyTag)) {
				statuStr = "新增";
			}else if ("1".equals(modifyTag)) {
				statuStr = "删除";
			}else if ("2".equals(modifyTag)) {
				statuStr = "变更";
			}
			
			String msg = staffName+"，您为"+ custGrps.getData(0).getString("GROUP_ID")+ custGrps.getData(0).getString("CUST_NAME") 
					+"集团办理的"+offerName+"产品"+statuStr+"操作已成功归档，请留意。";
			mainTrade.put("TRADE_STAFF_ID", merchpTrade.getString("UPDATE_STAFF_ID"));
			mainTrade.put("TRADE_DEPART_ID", merchpTrade.getString("UPDATE_DEPART_ID"));
			sendSMS(mainTrade,eparchyCode,phone, "-1", msg);
		}
		
	}
	
	/**
     * 发送短信
     * @param mainTrade
     * @param eparchyCode
     * @param serialNumber
     * @param userID
     * @param temp
     * @throws Exception
     */
    private static void sendSMS(IData mainTrade, String eparchyCode, String serialNumber, String userID, String temp) throws Exception {
        IData sendData = new DataMap();
        String sysdate = SysDateMgr.getSysTime();
        String smsNoticeId = SeqMgr.getSmsSendId();
        sendData.put("SMS_NOTICE_ID", smsNoticeId);
        sendData.put("PARTITION_ID", smsNoticeId.substring(smsNoticeId.length() - 4));
        sendData.put("EPARCHY_CODE", eparchyCode);
        sendData.put("RECV_OBJECT", serialNumber);// 需发短信的手机号
        sendData.put("RECV_ID", userID);
        sendData.put("NOTICE_CONTENT", temp);

        /*------------------------以下是原来写死的值，改用默认值--------------------------*/
        sendData.put("SEND_TYPE_CODE", "I0");
        sendData.put("SEND_COUNT_CODE", "1");// 发送次数编码?
        sendData.put("REFERED_COUNT", "0");// 发送次数？
        sendData.put("CHAN_ID", "11");
        sendData.put("RECV_OBJECT_TYPE", "00");// 00手机号
        sendData.put("SMS_TYPE_CODE", "20");//
        sendData.put("SMS_KIND_CODE", "02");// 02与SMS_TYPE_CODE配套
        sendData.put("NOTICE_CONTENT_TYPE", "0");// 0指定内容发送
        sendData.put("FORCE_REFER_COUNT", "1");// 指定发送次数
        sendData.put("SMS_PRIORITY", "50");// 短信优先级
        sendData.put("REFER_TIME", sysdate);// 提交时间
        sendData.put("REFER_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID", ""));// 员工ID
        sendData.put("REFER_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID", ""));// 部门ID
        sendData.put("DEAL_TIME", sysdate);// 完成时间
        sendData.put("DEAL_STATE", "0");// 处理状态，0：已处理，15未处理
        sendData.put("SEND_OBJECT_CODE", "6");// 通知短信,见TD_B_SENDOBJECT
        sendData.put("SEND_TIME_CODE", "1");// 营销时间限制,见TD_B_SENDTIME
        sendData.put("REMARK", mainTrade.getString("REMARK", ""));// 备注

        /*------------------------以下是原来没有写入的值--------------------------*/
        sendData.put("BRAND_CODE", mainTrade.getString("BRAND_CODE", ""));
        sendData.put("IN_MODE_CODE", mainTrade.getString("IN_MODE_CODE", ""));// 接入方式编码
        sendData.put("SMS_NET_TAG", mainTrade.getString("SMS_NET_TAG", "0"));
        sendData.put("FORCE_OBJECT", mainTrade.getString("FORCE_OBJECT", ""));// 发送方号码
        sendData.put("FORCE_START_TIME", mainTrade.getString("FORCE_START_TIME", ""));// 指定起始时间
        sendData.put("FORCE_END_TIME", mainTrade.getString("FORCE_END_TIME", ""));// 指定终止时间
        sendData.put("DEAL_STAFFID", mainTrade.getString("TRADE_STAFF_ID", ""));// 完成员工
        sendData.put("DEAL_DEPARTID", mainTrade.getString("TRADE_DEPART_ID", ""));// 完成部门
        sendData.put("REVC1", mainTrade.getString("REVC1", ""));
        sendData.put("REVC2", mainTrade.getString("REVC2", ""));
        sendData.put("REVC3", mainTrade.getString("REVC3", ""));
        sendData.put("REVC4", mainTrade.getString("REVC4", ""));
        sendData.put("MONTH", sysdate.substring(5, 7));// 月份
        sendData.put("DAY", sysdate.substring(8, 10)); // 日期

        Dao.insert("TI_O_SMS", sendData);
    }
}
