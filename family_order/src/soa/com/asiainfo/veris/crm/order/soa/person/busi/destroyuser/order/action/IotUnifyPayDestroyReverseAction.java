package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action;


import com.ailk.bizcommon.route.Route;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class IotUnifyPayDestroyReverseAction implements  ITradeFinishAction{

	public void executeAction(IData mainTrade) throws Exception {
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		IDataset configs = CommparaInfoQry.getCommparaByAttrCode1("CSM", "888", tradeTypeCode, Route.CONN_CRM_CEN, null);
		if (IDataUtil.isEmpty(configs)) {// 为空不处理
			return;
		} else {
			String paramCode = configs.getData(0).getString("PARAM_CODE"); // 停开机状态值
			if ("XIAOHU".equals(paramCode)) {// 处理销户
				processCancelAccountBiz(serialNumber);
			}
		}
	}

	/**
	 * 处理销户同步COMIT
	 * 
	 * @param btd
	 * @throws Exception
	 */
	private void processCancelAccountBiz(String serialNumber) throws Exception {
		// 查询欲处理的CMIOT记录
		IData param = new DataMap();
		param.put("PAY_NUMBER", serialNumber);
		StringBuilder sb = new StringBuilder("SELECT * FROM TF_A_CMIOT_PAYRELATION WHERE PAY_NUMBER=:PAY_NUMBER AND (STATE='3' OR STATE='1') ");
		IDataset cmiotList = Dao.qryBySql(sb, param, Route.CONN_CRM_CEN);
		if (IDataUtil.isNotEmpty(cmiotList)) {
			IDataset updList = new DatasetList();
			for (int i = 0; i < cmiotList.size(); i++) {
				IData cmiotData = cmiotList.getData(i);
				// 通知CMIOT
				IData res = notifyCmiotCenter(cmiotData, "03", "03");
				String bizCode = res.getString("BIZ_CODE");
				if ("0000".equals(bizCode)) {// 通知成功，更新数据
					cmiotData.put("STATE", "2");
					cmiotData.put("EFFECT_TIME", SysDateMgr.getSysTime());
					cmiotData.put("UPDATE_TIME", SysDateMgr.getSysTime());
					updList.add(cmiotData);
				} else {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "手机号码开机连带处理：接口同步CMIOT失败，请重试！");
				}
			}
			// 入库
			for (int i = 0; i < updList.size(); i++) {
				IData udpData = updList.getData(i);
				Dao.update("TF_A_CMIOT_PAYRELATION", udpData, new String[] { "PAY_ID", "PAY_NUMBER", "PAYEE_NUMBER" }, Route.CONN_CRM_CEN);
			}
		}
	}

	/**
	 * 通知CMIOT中心
	 * 
	 * @param cmiotData
	 * @param oprTag
	 *            销户 03 , OprType03-取消04-暂停05-恢复
	 * @return
	 * @throws Exception
	 */
	private IData notifyCmiotCenter(IData cmiotData, String oprTag, String oprType) throws Exception {
		IData resultData = new DataMap();
		resultData.put("CHANNEL", "01");
		resultData.put("BIZ_TYPE", cmiotData.getString("BIZ_TYPE", ""));
		resultData.put("OPR_SEQ", "731" + "000" + SysDateMgr.getSysDateYYYYMMDDHHMMSS() + (int) ((Math.random() * 9 + 1) * 100000));
		resultData.put("CUST_TYPE", cmiotData.getString("CUST_TYPE", ""));
		resultData.put("OPR_TYPE", oprType);
		resultData.put("PAY_ID", cmiotData.getString("PAY_ID", ""));
		resultData.put("EFFECT_TIME", SysDateMgr.getSysTime());
		resultData.put("REQ_REASON", oprTag);
		resultData.put("KIND_ID", "ReversePaymentReq_bizOrder_0_0");
		IDataset res = IBossCall.dealInvokeUrl("ReversePaymentReq_bizOrder_0_0", "IBOSS", resultData);
		return res.getData(0);
	}



}
