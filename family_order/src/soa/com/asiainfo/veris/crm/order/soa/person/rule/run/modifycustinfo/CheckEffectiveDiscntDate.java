package com.asiainfo.veris.crm.order.soa.person.rule.run.modifycustinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.FamilyOperPreBean;

public class CheckEffectiveDiscntDate extends BreBase implements IBREScript{

    private static final long serialVersionUID = 1L;
	
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		String userID = databus.getString("USER_ID");
		IDataset userDiscnt = FamilyOperPreBean.getDiscntCode(userID);
		if(IDataUtil.isNotEmpty(userDiscnt)){
			for (int i = 0; i < userDiscnt.size(); i++) {
				String discntCode =userDiscnt.getData(i).getString("DISCNT_CODE");
				IDataset discntCodes = CommparaInfoQry.getCommparaAllColByParser("CSM", "2256", discntCode, "ZZZZ");
				if(IDataUtil.isNotEmpty(discntCodes)){
					//CSAppException.apperr(CrmCommException.CRM_COMM_103, "该优惠["+discntCode+"]"+"在用户当前生效期内，不能办理过户、客户资料变更业务!");
					return true;
				}
			}
		}
		
		return false;
	}

}
