package com.asiainfo.veris.crm.order.soa.person.rule.run.changecustowner;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class CheckUserInternationBusiRule extends BreBase implements IBREScript{

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		String userId = databus.getString("USER_ID");
		
		//查询用户是否存在国际业务15,19
		IDataset svc15=UserSvcInfoQry.getSvcUserIdPf(userId, "15");
		if(IDataUtil.isNotEmpty(svc15)){
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, "", "业务规则提示！该号码有国际漫游业务话费押金，过户后将自动取消国际长途/漫游业务，是否继续办理");
        	return true;
		}else{
			IDataset svc19=UserSvcInfoQry.getSvcUserIdPf(userId, "19");
			if(IDataUtil.isNotEmpty(svc19)){
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, "", "业务规则提示！该号码有国际漫游业务话费押金，过户后将自动取消国际长途/漫游业务，是否继续办理");
	        	return true;
			}
		}
		
		return false;
	}
}
