package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.action.undofinish;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

/**
 * 光猫押金清退，退转到现金账户
 * @author zyc
 *
 */
public class UndoModemDepositAction implements ITradeFinishAction {

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		// TODO Auto-generated method stub

		// 是否有光猫
		String modemStyle = mainTrade.getString("RSRV_STR2");
		
		
		//判断用户是否已经成功付费
        boolean isPayCost = false;
        
        //非先装后付模的 默认已经付费成功
        if (!"A".equals(mainTrade.getString("RSRV_STR1")))
        {
        	isPayCost = true;
        }
        else
        {
        	if ("Y".equals(mainTrade.getString("RSRV_STR8")))
            {
            	isPayCost = true;
            }
        }

		if (StringUtils.isNotBlank(modemStyle)) {
			
			// 取TF_B_TRADE_OTHER RSRV_VALUE_CODE = FTTH
			String tradeId = mainTrade.getString("TRADE_ID");
			IDataset tradeOtherInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "FTTH");
			if (IDataUtil.isNotEmpty(tradeOtherInfos)) {
				IData modem = tradeOtherInfos.getData(0);
    		for(int i=0;i<tradeOtherInfos.size();i++){
    			if("0".equals(tradeOtherInfos.getData(i).getString("MODIFY_TAG"))){
    				modem = tradeOtherInfos.getData(i);
    			}
    		}
				
				
				String rsrvStr7 = modem.getString("RSRV_STR7","");
				String outTradeId = modem.getString("RSRV_STR8","");
				String rsrvTag1 = modem.getString("RSRV_TAG1",""); //0 租赁 ,1 购买 ,2 赠送 ,3 自备
				if(rsrvStr7 != null && "0".equals(rsrvStr7) && rsrvTag1 != null && "0".equals(rsrvTag1))
				{
					// 宽带撤单光猫押金退还处理
					IData param = new DataMap();
					String serialNumber = mainTrade.getString("SERIAL_NUMBER", "");
					if(serialNumber.startsWith("KD_"))
					{
						serialNumber = serialNumber.substring(3, serialNumber.length());
					}
					param.put("OUTER_TRADE_ID", outTradeId);
					param.put("SERIAL_NUMBER", serialNumber);
					param.put("TRADE_FEE", modem.getString("RSRV_STR2", "0"));
					param.put("CHANNEL_ID", "15000");
					param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
					param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
					param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
					param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		            param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		            
		            //获取宽带光猫押金存折 9002
		            int balance9002 = 0;
		        	IDataset allUserMoney = AcctCall.queryAccountDepositBySn(serialNumber);
		        	for(int i=0;i<allUserMoney.size();i++){
		        		if("9002".equals(allUserMoney.getData(i).getString("DEPOSIT_CODE"))){
		        			String balance1 = allUserMoney.getData(i).getString("DEPOSIT_BALANCE","0");
		                    int balance2 = Integer.parseInt(balance1);
		                    balance9002 = balance9002 + balance2;
		        		}
		        	}
		            //支付了费用并且存折里面有钱 才需要调用账务接口退费
		            if (isPayCost && balance9002 > 0 && null !=outTradeId && !"".equals(outTradeId))
		            {
		            	IData resultData = AcctCall.transFeeOutADSL(param);
						String result=resultData.getString("RESULT_CODE","");
				        if("".equals(result) || !"0".equals(result))
				        {
				        	CSAppException.appError("61312", "调用接口AM_CRM_TransFeeOutADSL退还押金入参：" + param + ",错误信息:" + resultData.getString("RESULT_INFO"));
				        }
		            }
		            
		            
			        
			        //更新 TF_B_TRADE_OTHER表的RsrvStr7，标记为 2 ，表示押金已退还
			        IData inParam = new DataMap();
			        inParam.put("TRADE_ID", tradeId);
			        inParam.put("REMARK", "光猫押金存折金额为"+balance9002);
			        Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_FTTH_BACKFEE_STATE", inParam, Route.getJourDb(BizRoute.getTradeEparchyCode()));
				}
			}
		}
	}
}
