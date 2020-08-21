package com.asiainfo.veris.crm.order.soa.script.rule.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class CheckOperStaffAndSerialNumberReg extends BreBase implements IBREScript{

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		String staffCityCode=CSBizBean.getVisit().getCityCode();
		
		IDataset userInfos=databus.getDataset("TF_F_USER");
		if(IDataUtil.isNotEmpty(userInfos)){
			String userCityCode=userInfos.getData(0).getString("CITY_CODE","");
			
			if(!(staffCityCode.equals("HNSJ")||staffCityCode.equals("HNHN")||
					staffCityCode.equals("HNYD"))
					&&(userCityCode.equals("HNSJ")||
					   userCityCode.equals("HNHN")||
					   userCityCode.equals("HNYD"))){
				return true;
			}
		}
		
		return false;
	}
}
