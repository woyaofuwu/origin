package com.asiainfo.veris.crm.order.soa.person.busi.thingsinternet.order.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class CallIBossAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		String serviceId = mainTrade.getString("RSRV_STR8");
		String operType = mainTrade.getString("RSRV_STR1");
		String userId = mainTrade.getString("USER_ID");
		
		IDataset result = UserResInfoQry.queryUserResByUserIdResType(userId, "1");
		if(IDataUtil.isEmpty(result))
		{
			//报错
		}
		IData resInfo = result.getData(0);
		
		IData param = new DataMap();
		param.put("ROUTETYPE", "00");
		param.put("ROUTEVALUE", "000");
		param.put("KIND_ID", "BIP6B901_T9898989_0_0");
		param.put("MSG_TYPE", "SyncOrderRelationReq");// 业务标识
		param.put("TRANSACTION_ID", mainTrade.getString("TRADE_ID"));// 操作流水
		if("1".equals(operType)
				|| "2".equals(operType)
				|| "3".equals(operType)
				|| "4".equals(operType))
		{
			param.put("ACTION_ID", operType);
		}
		else if("5".equals(operType) && "1".equals(mainTrade.getString("RSRV_STR2")))
		{
			
			
			param.put("ROUTETYPE", "00");
			param.put("ROUTEVALUE", "000");
			param.put("KIND_ID", "BIP6B902_T9898989_0_0");
			param.put("MSG_TYPE", "ServiceChangeReq");//业务标识
			param.put("TRANSACTION_ID", mainTrade.getString("TRADE_ID"));//操作流水
			param.put("CHANGE_TYPE", "2");//变更类型
			param.put("OLD_FEEUSER_ID", mainTrade.getString("RSRV_STR9"));//主号码
			param.put("OLD_DESTUSER_ID",  mainTrade.getString("SERIAL_NUMBER", ""));//从号码
			param.put("OLD_DESTUSER_IMSI", resInfo.getString("IMSI"));//从号码旧IMSI
			param.put("NEW_FEEUSER_ID", mainTrade.getString("RSRV_STR10"));//新主号码
			param.put("NEW_DESTUSER_ID",  mainTrade.getString("SERIAL_NUMBER", ""));//新从号码
			param.put("NEW_DESTUSER_IMSI", resInfo.getString("IMSI"));//新从号码IMSI			
			result = IBossCall.callHttpIBOSS7("IBOSS", param);
			if(IDataUtil.isEmpty(result))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口报错.KIND_ID=BIP6B902_T9898989_0_0！");
			}
			IData tmp = result.getData(0);
			if (!tmp.getString("RESULT","").equals("0")) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "同步给行车卫士平台信息出错，错误编码【" + tmp.getString("RESULT")+ "】!");
			}
			return;
		}
		
		param.put("PROVINCE_ID", "8981");// 
		param.put("FEEUSER_ID", mainTrade.getString("RSRV_STR6", ""));// 主号码
		param.put("DESTUSER_ID", mainTrade.getString("SERIAL_NUMBER", ""));// 从号码
		
		result = CommparaInfoQry.getCommpara("CSM", "5002", serviceId, CSBizBean.getTradeEparchyCode());
		
		if(IDataUtil.isNotEmpty(result)){
			param.put("SPSERVICE_ID", result.getData(0).getString("PARA_CODE5",""));// 行车卫士平台中该服务的服务代码
		}
		param.put("DESTUSER_IMSI", resInfo.getString("IMSI"));// 终端IMSI
		
		result = IBossCall.callHttpIBOSS7("IBOSS", param);
		if(IDataUtil.isEmpty(result))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口报错.KIND_ID=BIP6B902_T9898989_0_0！");
		}
		IData tmp = result.getData(0);
		if (!tmp.getString("RESULT","").equals("0")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "同步给行车卫士平台信息出错，错误编码【" + tmp.getString("RESULT")+ "】!");
		}
	}

}
