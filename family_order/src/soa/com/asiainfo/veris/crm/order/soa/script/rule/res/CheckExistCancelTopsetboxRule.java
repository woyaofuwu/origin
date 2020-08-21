package com.asiainfo.veris.crm.order.soa.script.rule.res;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class CheckExistCancelTopsetboxRule extends BreBase implements IBREScript{

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		String userId=databus.getString("USER_ID");
		IDataset rollBackTopsetboxs = UserResInfoQry.queryRollbackTopSetBox(userId);
		if(IDataUtil.isNotEmpty(rollBackTopsetboxs)){
			return true;
		}
		
		return false;
	}
	
}
