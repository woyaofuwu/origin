package com.asiainfo.veris.crm.order.soa.person.busi.topsetboxmanage.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TopSetBoxOccupyAction.java
 * @Description: 机顶盒占用或换机或退还
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-8-5 下午9:31:30 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
 */
public class TopSetBoxOccupyAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
    	String actionType = mainTrade.getString("RSRV_STR1"); // 0:购买 1:换机  2:退还 3:丢失
    	if("0".equals(actionType) || "1".equals(actionType)){
    		IDataset returnResult = HwTerminalCall.saleOrChange4SetTopBox(mainTrade);
    		if(IDataUtil.isEmpty(returnResult)){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口报错！");
			}else{
				String resultCode=returnResult.getData(0).getString("X_RESULTCODE","");
				if(!resultCode.equals("0")){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口错误："+returnResult.
							getData(0).getString("X_RESULTINFO",""));
				}
			}
    	}else if("2".equals(actionType)){
    		IData returnParam = new DataMap();
    		String trade_id = mainTrade.getString("TRADE_ID");
    		IDataset boxInfos = TradeResInfoQry.queryAllTradeResByTradeId(trade_id);
    		if(DataSetUtils.isNotBlank(boxInfos)){
    			IData boxInfo = boxInfos.first();
    			String serialNumber = mainTrade.getString("SERIAL_NUMBER");
    			returnParam.put("RES_NO", boxInfo.getString("IMSI"));
				returnParam.put("PARA_VALUE1", serialNumber);
				returnParam.put("SALE_FEE", boxInfo.getString("RSRV_NUM5",""));
				returnParam.put("PARA_VALUE7", "0");
				returnParam.put("DEVICE_COST", boxInfo.getString("RSRV_NUM4","0"));
				returnParam.put("X_CHOICE_TAG", "1");
				returnParam.put("RES_TYPE_CODE", "4");
				returnParam.put("PARA_VALUE11", boxInfo.getString("UPDATE_TIME"));
				returnParam.put("PARA_VALUE14", boxInfo.getString("RSRV_NUM5","0"));
				returnParam.put("PARA_VALUE17", boxInfo.getString("RSRV_NUM5","0"));
				returnParam.put("PARA_VALUE1", serialNumber);
				returnParam.put("USER_NAME", mainTrade.getString("CUST_NAME"));
				returnParam.put("STAFF_ID", boxInfo.getString("UPDATE_STAFF_ID"));
				returnParam.put("TRADE_ID", boxInfo.getString("INST_ID"));
    			IDataset returnResult=HwTerminalCall.returnTopSetBoxTerminal(returnParam);
				if(IDataUtil.isEmpty(returnResult)){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口报错！");
				}else{
					String resultCode=returnResult.getData(0).getString("X_RESULTCODE","");
					if(!resultCode.equals("0")){
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口错误："+returnResult.
								getData(0).getString("X_RESULTINFO",""));
					}
				}
    		}
    	}
    }
}
