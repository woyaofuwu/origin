package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order.action;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.trade.OneCardMultiNoTradeUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order.requestdata.RealNameRegReqData;

/**
 * 和多号虚拟副号实名补录在此处理
 * 
 */
public class ChangeMospUserCheckAction  implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		UcaData uca = btd.getRD().getUca();
		String tradeTypeCode = btd.getTradeTypeCode();
		String brandCode = uca.getBrandCode();
		String snb = uca.getSerialNumber();
		
		if(StringUtils.equals("MOSP", brandCode) && StringUtils.equals("62", tradeTypeCode))
		{
			IDataset bindInfos = this.getHDHBindInfo(snb);
			if(IDataUtil.isEmpty(bindInfos))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"未找到该副号码的绑定记录，无法实名补录！");
			}
			IData data = bindInfos.getData(0);
			String mainCustName = data.getString("CUST_NAME");
			String mainPsptId = data.getString("PSPT_ID");
			String mainPsptTypeCode = data.getString("PSPT_TYPE_CODE");
			
			RealNameRegReqData realNameRD = (RealNameRegReqData) btd.getRD();
			
			if(!StringUtils.equals(mainCustName, realNameRD.getCustName()) 
					||!StringUtils.equals(mainPsptTypeCode, realNameRD.getPsptTypeCode()) 
					|| !StringUtils.equals(mainPsptId, realNameRD.getPsptId()))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"要补录的实名信息与主卡信息不一致！");
			}
			
			IData inparam = new DataMap();
			inparam.put("SERIAL_NUMBER",  data.getString("SERIAL_NUMBER"));
			inparam.put("OPER_CODE", "08");
			IDataset follows = new DatasetList();
			IData followData = new DataMap();
			followData.put("FOLLOW_MSISDN", snb);
			followData.put("SERIAL_NUM", data.getString("ORDERNO"));
			followData.put("MOSP_CATEGORY", "0");
			follows.add(followData);
			inparam.put("FOLLOW_INFOLIST", follows);
			inparam.put("CATEGORY", "0");
			
			OneCardMultiNoTradeUtil.updateRelationsCallIBossNew(inparam);
		}
	}
	
	public IDataset getHDHBindInfo(String snb)throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TP_F_HDH_SYNINFO WHERE SERIAL_NUMBER_B =:SERIAL_NUMBER_B and end_date >sysdate");
		
		IData data = new DataMap();
		data.put("SERIAL_NUMBER_B", snb);
		
		return Dao.qryBySql(sql, data,Route.CONN_CRM_CEN);
	}
}
