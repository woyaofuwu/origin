package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/*
 * 不允许发起物联网集团客户新增 
 */

public class CheckWLWGrpCreateCustLimit extends BreBase implements IBREScript{

	private static final Logger logger = Logger.getLogger(CheckWLWGrpCreateCustLimit.class);
	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		if(logger.isDebugEnabled()){
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckWLWGrpCreateCustLimit() databus>>>>>>>>>>>>>>>>>>"+databus);
		}
		String errCode = databus.getString("RULE_BIZ_ID");
		String brandCode = databus.getString("BRAND_CODE");
		if("WLWG".equals(brandCode)){
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "目前不允许发起物联网产品订购操作 ");
			return true;
		}
		return false;
	}

}
