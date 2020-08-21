package com.asiainfo.veris.crm.order.web.person.remotedestroyuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

public abstract class AuditDestroyOrder extends PersonBasePage{
	private static final Logger logger = Logger.getLogger(AuditDestroyOrder.class);

	public void qryCustInfo(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		String serialNumber = data.getString("SERIAL_NUMBER");
		String orderId = data.getString("ORDER_ID");
		String allStatusTag = data.getString("ALL_STATUS_TAG","");
		String deal_tag = data.getString("DEAL_TAG","");
		IData queryParam = new DataMap();
		if(StringUtils.isNotBlank(allStatusTag)){
			queryParam.put("SERIAL_NUMBER", serialNumber);
			queryParam.put("ORDER_ID", orderId);
			queryParam.put("DEAL_TAG", deal_tag);
			queryParam.put("ALL_STATUS_TAG", allStatusTag);
		}else{
			
			queryParam.put("SERIAL_NUMBER", serialNumber);
			queryParam.put("ORDER_ID", orderId);
			//queryParam.put("DEAL_TAG", "0");
		}
		//校验是否有销户工单
		IDataset receiptOrders = CSViewCall.call(this, "SS.RemoteDestroyUserSVC.queryReceiptOrder", queryParam);
		if(DataSetUtils.isBlank(receiptOrders)) {
			CSViewException.apperr(CrmCommException.CRM_COMM_159);
		}
		IData receiptOrder = receiptOrders.first();
		setReceiptOrder(receiptOrder);
		String outerTradeId = receiptOrder.getString("ORDER_ID");
		IData acctInParam = new DataMap();
		acctInParam.put("SERIAL_NUMBER", receiptOrder.getString("SERIAL_NUMBER"));
		acctInParam.put("OUTER_TRADE_ID", outerTradeId);
		IDataset transferInfos = CSViewCall.call(this, "SS.RemoteDestroyUserSVC.queryTransferInfo", acctInParam);
		IData transferInfo = transferInfos.first();
		String transMoney = transferInfo.getString("TRANS_MONEY");
		String transPay = transferInfo.getString("TRANS_PAY");
		//单位由分转为元，小数点后保留角分
		transMoney = fenToYuan(transMoney);
		transPay = fenToYuan(transPay);
		IData transferAmount = new DataMap();
		transferAmount.put("TRANS_MONEY", transMoney);
		transferAmount.put("TRANS_PAY", transPay);
		setTransferAmount(transferAmount);
		setAjax(receiptOrder);
	}
	
	public void onTradeSubmit(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		String serialNumber = data.getString("SERIAL_NUMBER");
		String orderId = data.getString("ORDER_ID");
		String auditResult = data.getString("AUDIT_RESULT");
		String checkResultNum = data.getString("CHECK_RESULT");
		IData queryParam = new DataMap();
		if(StringUtils.isNotBlank(serialNumber)){
			queryParam.put("SERIAL_NUMBER", serialNumber);
		}
		queryParam.put("ORDER_ID", orderId);
		queryParam.put("DEAL_TAG", "0");
		//校验是否有销户工单
		IDataset cancelOrder = CSViewCall.call(this, "SS.RemoteDestroyUserSVC.queryReceiptOrder", queryParam);
		if(IDataUtil.isEmpty(cancelOrder)){
			CSViewException.apperr(CrmCommException.CRM_COMM_301, "查询此号码"+serialNumber+"无接单工单数据！");
		}
		data.put("TIP_INFO", data.getString("AUDIT_REASON"));
		data.put("BIZ_ORDER_RESULT", "0000");
		serialNumber = cancelOrder.getData(0).getString("SERIAL_NUMBER");
		//准备同步资料
		data.put("CANCEL_ORDER", cancelOrder);
		if("0".equals(auditResult)){
			if("2".equals(checkResultNum)||"3".equals(checkResultNum)||"4".equals(checkResultNum)){
				data.put("BIZ_ORDER_RESULT", "3029");
			}else{
				data.put("BIZ_ORDER_RESULT", "3006");
			}
		}
		IData syncContent = CSViewCall.call(this, "SS.RemoteDestroyUserSVC.applyCancelAccountSyn", data).first();
		//更新接单表
		updateReceiptOrder(data, syncContent);
		if("1".equals(auditResult)){
			IData nowParam = new DataMap();
			nowParam.put("SERIAL_NUMBER", serialNumber);
			nowParam.put("ORDER_ID", orderId);
			nowParam.put("DEAL_TAG", "1");
			IDataset auditedOrder = CSViewCall.call(this, "SS.RemoteDestroyUserSVC.qryAuditedOrder", nowParam);
			if(IDataUtil.isNotEmpty(auditedOrder)){
				IData data2 = new DataMap();
				IData autoDestroyOrder = auditedOrder.getData(0);
				if(StringUtils.isNotBlank(autoDestroyOrder.getString("CREATE_STAFF_ID"))&&StringUtils.isNotBlank(autoDestroyOrder.getString("CREATE_DEPART_ID"))){
					data2.put("CREATE_DEPART_ID", autoDestroyOrder.getString("CREATE_DEPART_ID"));
					data2.put("CREATE_CITY_CODE", autoDestroyOrder.getString("CREATE_CITY_CODE"));
					data2.put("CREATE_STAFF_ID", autoDestroyOrder.getString("CREATE_STAFF_ID"));
				}else{
					data2.put("CREATE_DEPART_ID", "00309");
					data2.put("CREATE_CITY_CODE", "INTF");
					data2.put("CREATE_STAFF_ID", "IBOSS000");
				}
				data2.put("TRADE_TYPE_CODE", "192");//立即销户
				data2.put("SERIAL_NUMBER", serialNumber);
				//data.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);//预受理
				data2.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
				IDataset dataset = CSViewCall.call(this,"SS.DestroyUserNowRegSVC.tradeReg", data2);
			}
		}
	}

	private String fenToYuan(String fen) {
		String yuan;
		int strSize = fen.length();
		if(strSize > 2){
			yuan = fen.substring(0, strSize - 2) +
					"." + fen.substring(strSize - 2);
		} else if (strSize == 2) {
			yuan = "0." + fen;
		} else {
			yuan = "0.0" + fen;
		}
		return yuan;
	}

	private IDataset updateReceiptOrder(IData data, IData syncContent) throws Exception {
		IData updateInfo = new DataMap();
		//接单表更新cash_amount=转账现金金额，nocash_amount=转账非现金金额,order_id为主键
		String checkResult = data.getString("CHECK_RESULT");
		updateInfo.put("ORDER_ID", syncContent.getString("OPR_NUMB"));
		updateInfo.put("CASH_AMOUNT", syncContent.getString("TRANS_MONEY_FEN"));
		updateInfo.put("NOCASH_AMOUNT", syncContent.getString("TRANS_PAY_FEN"));
		updateInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
		updateInfo.put("FINISH_TIME", SysDateMgr.getSysTime());
		String auditResult = data.getString("AUDIT_RESULT");
		String auditReason = data.getString("AUDIT_REASON");
		if("0".equals(auditResult)) {
			//审核失败时，接单表更新deal_tag=3，rsrv_str1=失败原因
			updateInfo.put("RSRV_STR1", auditReason);
			updateInfo.put("RSRV_STR5", checkResult);//工单处理结果  0：销户成功 1：用户原因销户失败 2：销户请求报文填写错误 3：销户附件缺失 4：销户附件不可用
			if("2".equals(checkResult)||"3".equals(checkResult)||"4".equals(checkResult)){
				updateInfo.put("DEAL_TAG", "3");
				updateInfo.put("DEAL_INFO", "审核退回");
			}else{
				updateInfo.put("DEAL_TAG", "2");
				updateInfo.put("DEAL_INFO", "处理失败");
			}
		} else {
			//审核成功时，deal_tag=1
			updateInfo.put("RSRV_STR5", "0");//工单处理结果  0：销户成功 1：用户原因销户失败 2：销户请求报文填写错误 3：销户附件缺失 4：销户附件不可用
			updateInfo.put("DEAL_TAG", "1");
			updateInfo.put("DEAL_INFO", "用户已具备销户条件");
			updateInfo.put("RSRV_STR1", "待销户");
		}
		return CSViewCall.call(this, "SS.RemoteDestroyUserSVC.updateReceiveDestroyUserTrade", updateInfo);
	}
	private IDataset updateDestroyFail(String orderId,String failMsg)throws Exception{
		IData destroyparam = new DataMap();
		destroyparam.put("ORDER_ID", orderId);
		destroyparam.put("UPDATE_TIME", SysDateMgr.getSysTime());
		destroyparam.put("RSRV_STR1","自动销户失败，请手动销户");
		destroyparam.put("RSRV_STR3",failMsg);
		destroyparam.put("DEAL_TAG","1");
		return CSViewCall.call(this, "SS.RemoteDestroyUserSVC.updateDestroyFail", destroyparam);
	}

	public abstract void setReceiptOrder(IData receiptOrder);
	public abstract void setTransferAmount(IData transferAmount);
}
