package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.order.action;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.OneNoMultiTerminalConstants;
/**
 * 停开机业务处理副设备uu关系表以及副设备的停开机

 *
 */
public class DealOpenAndCloseAuxRelationAction implements ITradeAction
 {

	public void executeAction(BusiTradeData btd) throws Exception 
	{
		IData inParam = new DataMap();
		inParam.put("USER_ID", btd.getRD().getUca().getUserId());
		inParam.put("RELATION_TYPE_CODE", OneNoMultiTerminalConstants.RELATION_TYPE_CODE);	
		
		IDataset relationListTmp=RelaUUInfoQry.qryRelationList(inParam);
		
		if(IDataUtil.isNotEmpty(relationListTmp))
		{
			String tradeTypeCode = btd.getTradeTypeCode();
			
			//para_code1 业务类型，
			//para_cade2操作类型，04业务暂停，05业务恢复
			//para_code3操作标志，1:前台暂停 2：前台恢复 3：欠费暂停（信控发起的停机） 4：缴费恢复 （信控发起的开机） 5.其他停机：主动停机 6.其他开机：主动开机
			 IDataset commparaSet = CommparaInfoQry.getCommparaByCode1("CSM", "1782", "ONMT", tradeTypeCode,null);
			 if(IDataUtil.isNotEmpty(commparaSet))
			 {
				 IData com = commparaSet.getData(0);
				 String oprCode = com.getString("PARA_CODE2");
				 String oprFlag = com.getString("PARA_CODE3");
				 callAuxDataStopServ(btd, relationListTmp, oprCode, oprFlag);
			 }
			
		}
}

	private void callAuxDataStopServ(BusiTradeData btd,IDataset relationListTmp, String oprCode, String oprFlag) throws Exception {
		IDataset auxCodes = new DatasetList();
		for(int i = 0;i < relationListTmp.size();i++)
		{
			IData relation = relationListTmp.getData(i);
			IData auxInfo = new DataMap();
			String rsrvStr1 = relation.getString("RSRV_STR1","");//操作类型：01-业务添加 02-业务删除 04-业务暂停 05-业务恢复
			String channlCode = relation.getString("RSRV_NUM1","");//业务暂停方式：1--业务主动发起的业务暂停操作
			if(OneNoMultiTerminalConstants.OPER_CODE_SUSPEND.equals(oprCode))
			{   
				//拼业务暂停数据
				if((rsrvStr1.equals(OneNoMultiTerminalConstants.OPER_CODE_ADD) || rsrvStr1.equals(OneNoMultiTerminalConstants.OPER_CODE_RESUME)) && "".equals(channlCode))
				{					
					auxInfo.put("ORDERNO", relation.getString("ORDERNO",""));
					auxInfo.put("INST_ID", relation.getString("INST_ID",""));
					auxInfo.put("USER_ID_B", relation.getString("USER_ID_B",""));
					auxInfo.put("AUX_NICK_NAME",relation.getString("RSRV_STR5",""));
					auxInfo.put("SERIAL_NUMBER_B",relation.getString("SERIAL_NUMBER_B",""));
					auxInfo.put("OPR_CODE","04");
					auxCodes.add(auxInfo);
				}
			}
			else 
			{	//拼业务恢复数据,只能恢复非前台一号多终端发起的暂停
				if (rsrvStr1.equals(OneNoMultiTerminalConstants.OPER_CODE_SUSPEND) && !"1".equals(channlCode))
				{
					auxInfo.put("ORDERNO", relation.getString("ORDERNO",""));
					auxInfo.put("INST_ID", relation.getString("INST_ID",""));
					auxInfo.put("USER_ID_B", relation.getString("USER_ID_B",""));
					auxInfo.put("AUX_NICK_NAME",relation.getString("RSRV_STR5",""));
					auxInfo.put("SERIAL_NUMBER_B",relation.getString("SERIAL_NUMBER_B",""));
					auxInfo.put("OPR_CODE","05");
					auxCodes.add(auxInfo);
				}
			}						
		}
		
		if(IDataUtil.isNotEmpty(auxCodes))
		{
			IData reqData = new DataMap();
			reqData.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
			reqData.put("OPR_FLAG", oprFlag);
			reqData.put("AUX_CODES", auxCodes);
			
			CSAppCall.call("SS.SingleNumMultiDeviceStatusChangeRegSVC.tradeReg", reqData);
		}
		
	}
}
