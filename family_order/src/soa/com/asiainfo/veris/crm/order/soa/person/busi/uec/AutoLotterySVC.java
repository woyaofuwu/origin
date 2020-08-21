package com.asiainfo.veris.crm.order.soa.person.busi.uec;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class AutoLotterySVC extends CSBizService {
	public IDataset autoLottery(IData mainTrade) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		//不触发自动抽奖的业务
		//优惠
		IDataset tradeDiscnts = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
		if (IDataUtil.isNotEmpty(tradeDiscnts)){
			for (int i = 0, size = tradeDiscnts.size(); i < size; i++){
				String discntCode = tradeDiscnts.getData(i).getString("DISCNT_CODE");
				if ("5833".equals(discntCode)){
					return null;
				}
			}
		}	
		
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String inModeCode = mainTrade.getString("IN_MODE_CODE");
		String autoLotteryConfigs = StaticUtil.getStaticValue("UECLOTTERY_AUTOLOTTERY_CONFIG", "ACTIVITY_NUMBERS");

		if(StringUtils.isBlank(autoLotteryConfigs)){
			IDataset autoLotteryResult = new DatasetList();
			IData returnData = new DataMap();
			returnData.put("X_RESULTCODE", "0");
			returnData.put("X_RESULTINFO", "没有需要进行自动抽奖的活动");
			autoLotteryResult.add(returnData);
			return autoLotteryResult;
		}
		
		String[] activityNumbers = autoLotteryConfigs.split(",");
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < activityNumbers.length; i ++){
			IData params = new DataMap();
			params.put("ACTIVITY_NUMBER", activityNumbers[i]);
			params.put("SERIAL_NUMBER", serialNumber);
			params.put("IN_MODE_CODE", inModeCode);
			IDataset results = CSAppCall.call("SS.UECLotteryGeneralSVC.lottery", params, true);

			if(IDataUtil.isEmpty(results)) continue;
			IData result = results.getData(0);
			String xResultCode = result.getString("X_RESULTCODE", "");
			sb.append("用户：");
			sb.append(serialNumber);
			sb.append("活动编码：");
			sb.append(activityNumbers[i]);
			sb.append(" 系统触发自动抽奖!");
			sb.append(" 结果：");
			sb.append(xResultCode);
			sb.append(" ");
			sb.append(result.getString("X_RESULTINFO", ""));
			sb.append("|");
			String smsContent = "";
			if("800120".equals(xResultCode)){
				smsContent = "尊敬的客户，您好！本月您已参加过10086热线“体验自助，畅享好礼 ”营销活动，活动礼品每个号码每月仅送一次，感谢您的参与。如有疑问，详询10086。";
			}else if("800200".equals(xResultCode)){
				smsContent = "尊敬的客户，您好！感谢您参与10086热线“体验自助，畅享好礼 ”营销活动，活动礼品有限，先到先得，本月礼品已送完，感谢您的参与。如有疑问，详询10086。";
			}else if("0".equals(xResultCode)){
				IData discntData = new DataMap();
				discntData.put("ELEMENT_ID", "5833");
				discntData.put("ELEMENT_TYPE_CODE", "D");
				discntData.put("SERIAL_NUMBER", serialNumber);
				discntData.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
				discntData.put("IN_MODE_CODE", inModeCode);
				discntData.put("BOOKING_TAG", "0");
				CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", discntData, true);
				
		        // 修改TF_B_TRADE_UECLOTTERY
		        IData param = new DataMap();
		        param.put("ACTIVITY_NUMBER", activityNumbers[i]);
		        param.put("USER_ID", mainTrade.get("USER_ID"));
		        param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		        param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
		        param.put("UPDATE_TIME", SysDateMgr.getSysTime());
		        param.put("EXEC_TIME", SysDateMgr.getSysTime());
		        param.put("EXEC_FLAG", "1");

		        StringBuilder sql = new StringBuilder(1000);

		        sql.append(" UPDATE TF_B_TRADE_UECLOTTERY A SET  ");
		        sql.append(" A.EXEC_FLAG= :EXEC_FLAG ");
		        sql.append(" ,A.UPDATE_STAFF_ID= :UPDATE_STAFF_ID ");
		        sql.append(" ,A.UPDATE_DEPART_ID= :UPDATE_DEPART_ID ");
		        sql.append(" ,A.UPDATE_TIME= TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') ");
		        sql.append(" ,A.EXEC_TIME= TO_DATE(:EXEC_TIME,'YYYY-MM-DD HH24:MI:SS') ");
		        sql.append(" WHERE A.ACTIVITY_NUMBER= :ACTIVITY_NUMBER AND A.USER_ID= :USER_ID ");
		        sql.append(" AND A.DEAL_FLAG='1' AND A.EXEC_FLAG='0' ");

		        Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
				
				smsContent = "尊敬的客户，您好！感谢您参与10086热线“体验自助，畅享好礼 ”营销活动，恭喜您获得价值3元的10M流量加油包业务，将于24小时内赠送，当月生效使用，月底自动取消，每个号码每月仅送一次。如有疑问，详询10086。";
			}else{
				continue;
			}
			
			if(!"".equals(smsContent)){
                IData smsData = new DataMap();
                smsData.clear();
                
                smsData.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE"));
                smsData.put("RECV_OBJECT", serialNumber);
                smsData.put("RECV_ID", mainTrade.getString("USER_ID"));
                smsData.put("NOTICE_CONTENT", smsContent);
                smsData.put("REMARK", "自动抽奖下发短信");
                SmsSend.insSms(smsData);
			}			
		}
		IDataset autoLotteryResult = new DatasetList();
		IData returnData = new DataMap();
		returnData.put("X_RESULTCODE", "0");
		returnData.put("X_RESULTINFO", sb.toString());
		autoLotteryResult.add(returnData);
		return autoLotteryResult;
	}
}
