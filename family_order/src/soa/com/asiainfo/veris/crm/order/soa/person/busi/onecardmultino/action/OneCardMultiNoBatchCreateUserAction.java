package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;



import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.OneCardMultiNoBean;


@SuppressWarnings("unchecked")
public class OneCardMultiNoBatchCreateUserAction implements ITradeFinishAction {
	@Override
	public void executeAction(IData mainTrade) throws Exception {
		String brandCode = mainTrade.getString("BRAND_CODE");
		if ("MOSP".equals(brandCode)) {
			String tradeId = mainTrade.getString("TRADE_ID");
			String serailNumber = mainTrade.getString("SERIAL_NUMBER");
			String eparchyCode = mainTrade.getString("EPARCHY_CODE");
			// 获取业务台帐资源子表
			IDataset tradeResInfos = TradeResInfoQry.queryTradeResByTradeIdAndModifyTag(tradeId,BofConst.MODIFY_TAG_ADD);
			
			if (IDataUtil.isEmpty(tradeResInfos)) {
				return;
			}
			String imsi = null;
			String msisdn_type = "";
			for (int j = 0; j < tradeResInfos.size(); j++) {
				if ("1".equals(tradeResInfos.getData(j).getString("RES_TYPE_CODE"))) {
					imsi = tradeResInfos.getData(j).getString("IMSI");
					// 0电话号码 和多号需求改造
				}else if("0".equals(tradeResInfos.getData(j).getString("RES_TYPE_CODE"))){
					msisdn_type = tradeResInfos.getData(j).getString("RSRV_STR2");
				}
			}
			
			IData inputInfo = new DataMap();
			inputInfo.put("SERIAL_NUMBER", serailNumber); // 虚拟副号码
			inputInfo.put("IMSI", imsi);// IMSI
			inputInfo.put("AREA_CODE", "898"); // 省编码
			inputInfo.put("EPARCHY_CODE", eparchyCode); // 地州编码
			String cancelTag = mainTrade.getString("CANCEL_TAG","0");
			//开户返销
			if("2".equals(cancelTag)){
				inputInfo.put("EXEC_STATUS", "1");// 号码状态 0-开户 1-销户
				inputInfo.put("OPERTYPE", "1"); // 同步类型 0-批量开销户 1-待销户批量
				inputInfo.put("SPRING_RESULT_CODE", "0"); // 处理结果
				inputInfo.put("USER_STATUS", "90"); // 用户状态
			}else{
				inputInfo.put("EXEC_STATUS", "0");// 号码状态 0-开户 1-销户
				inputInfo.put("OPERTYPE", "0"); // 同步类型 0-批量开销户 1-待销户批量
				inputInfo.put("SPRING_RESULT_CODE", ""); // 处理结果
				inputInfo.put("USER_STATUS", ""); // 用户状态
			}
			inputInfo.put("DEAL_STATUS", "0"); // 同步状态 0-未上传 1-已上传
			inputInfo.put("MSISDN_TYPE", msisdn_type);
			inputInfo.put("CLASS_ID", OneCardMultiNoBean.getNumberLevel(serailNumber)); //吉祥号码等级
			Dao.executeUpdateByCodeCode("TF_F_USER_RES","INS_MOSP_FOLLOWMSISDN",inputInfo,Route.CONN_CRM_CEN);
		}
	}
}