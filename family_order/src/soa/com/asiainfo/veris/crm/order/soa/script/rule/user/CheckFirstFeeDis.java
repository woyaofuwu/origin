package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class CheckFirstFeeDis extends BreBase implements IBREScript 
{

	private static Logger logger = Logger.getLogger(CheckFirstFeeDis.class);
	
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckFirstFeeDis() >>>>>>>>>>>>>>>>>>");

		boolean bResult = false;
		String strSerialNumber = databus.getString("SERIAL_NUMBER");
		
		IData idParam = new DataMap();
		idParam.put("SERIAL_NUMBER", strSerialNumber);
		//调用账务接口
		IDataset idsQryFPOneTimePay = CSAppCall.call("AM_CRM_QryFPOneTimePay", idParam);
		if (IDataUtil.isNotEmpty(idsQryFPOneTimePay)) 
		{
			boolean bIsTipsInfo = false;
			String strTipsInfo = "该号码有一次性收费套餐，";
			for (int i = 0; i < idsQryFPOneTimePay.size(); i++) 
			{
				IData idQryFPOneTimePay = idsQryFPOneTimePay.getData(i);
				String strFeePolicyID = idQryFPOneTimePay.getString("FEEPOLICY_ID", "");
				String strFeePolicyName = idQryFPOneTimePay.getString("FEEPOLICY_NAME", "");
				if(i==0)
				{
					strTipsInfo = strTipsInfo + strFeePolicyName + "[" + strFeePolicyID + "]";
				}
				else
				{
					strTipsInfo = strTipsInfo + "、" + strFeePolicyName + "[" + strFeePolicyID + "]";
				}
				//String strFeePolicy =  String.format("|%s[%s]", strFeePolicyName, strFeePolicyID);
				bIsTipsInfo = true;
				
			}
			if(bIsTipsInfo)
			{
				strTipsInfo = strTipsInfo + "，报开后系统将进行扣费，是否继续办理。";
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, "", strTipsInfo);
				return true;
			}
			
		}
		
		if (logger.isDebugEnabled())
			logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckFirstFeeDis() " + bResult + "<<<<<<<<<<<<<<<<<<<");
		
		return bResult;
	}
}
