package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;


import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.pccbusiness.PCCBusinessQry;

public class ChangeDiscntExpireSVC extends CSBizService{
	
	protected static final Logger log = Logger.getLogger(ChangeDiscntExpireSVC.class);

	public IDataset dealExpire(IData mainTrade) throws Exception
	{
		IDataset result = new DatasetList();
		String tradeId = mainTrade.getString("TRADE_ID");

		// 查历史台账 如存在未返销的 才进行处理
		IData mainHiTrade = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);
		if (IDataUtil.isNotEmpty(mainHiTrade))
		{
			//绑定服务
			result=this.processExecution(mainTrade);
		}

		return result;
	}
	
	public IDataset processExecution (IData mainTrade) throws Exception
	{
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String tradeStaffId = mainTrade.getString("TRADE_STAFF_ID");
		String tradeDepartId = mainTrade.getString("TRADE_DEPART_ID");
		String tradeCityCode = mainTrade.getString("TRADE_CITY_CODE");
		String startDate = mainTrade.getString("EXEC_TIME");
		
		IDataset result = new DatasetList();
		IData inParamNew = new DataMap();
		inParamNew.put("USER_ID", mainTrade.getString("USER_ID"));
		inParamNew.put("USRIDENTIFIER", "86"+mainTrade.getString("SERIAL_NUMBER"));
		inParamNew.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		
		IDataset params= PCCBusinessQry.qryPccOperationTypeForSubscriber(inParamNew);
		if (IDataUtil.isNotEmpty(params)) {
			
			String usrStatus = params.getData(0).getString("USR_STATUS", "");
			String execState = params.getData(0).getString("EXEC_STATE", "");
			//usrStatus 1为解速标识；2、3、4、6、8为限速标识
			//execState 处理状态 0-入库、1-处理中、2-处理完成、9-处理失败
			if (("2".equals(usrStatus) ||"3".equals(usrStatus) ||"4".equals(usrStatus)||
				"6".equals(usrStatus) ||"8".equals(usrStatus)) && "2".equals(execState)) 
			{
				IData svcParam = new DataMap();
				svcParam.put("SERIAL_NUMBER", serialNumber);
        		svcParam.put("ELEMENT_ID", "84071642");
        		svcParam.put("ELEMENT_TYPE_CODE", "S");
        		svcParam.put("BOOKING_TAG", "0");
        		svcParam.put("MODIFY_TAG", "0");
        		svcParam.put("ELEMENT_NOT_IN_PROD_PACK_FLAG", "N");
        		svcParam.put("START_DATE", startDate);
        		svcParam.put("END_DATE", SysDateMgr.getLastDateThisMonth4WEB());
        		svcParam.put("TRADE_STAFF_ID", tradeStaffId);
        		svcParam.put("TRADE_DEPART_ID", tradeDepartId);
        		svcParam.put("TRADE_CITY_CODE", tradeCityCode);
        		svcParam.put("TRADE_TYPE_CODE", "110");
				
        		IDataset callData = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", svcParam);

				result.add(callData);
				/*
				//发起解限速指令
				IData pccParam = new DataMap();
		        pccParam.put("USER_ID", mainTrade.getString("USER_ID"));
		        pccParam.put("OPERATOR_MIND", "3");
		        pccParam.put("RSRV_STR6", "dlxsjx");
		        pccParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
                try{
                	CSAppCall.call("SS.PccActionSVC.pccIntf", pccParam);
                
                }catch(Exception ex){
                 log.error("pccErr= "+ex.getMessage());
                }*/
				
			}
		}
		return result;
	}
	
}
