package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class CheckWLWGrpMembAddLimit extends BreBase implements IBREScript {
	private final static Logger logger = Logger.getLogger(CheckWLWGrpMembAddLimit.class);
	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		if(logger.isDebugEnabled()){
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckWLWGrpMembAddLimit() databus>>>>>>>>>>>>>>>>>>"+databus);
		}
		if(!"PWLW".equals(databus.getString("BRAND_CODE_B"))){
			return false;
		}
		String errCode = databus.getString("RULE_BIZ_ID");
		String userIdA = databus.getString("USER_ID");
		String productId = databus.getString("PRODUCT_ID");
		String relationTypeCode = "";
		IDataset queryOfferComChaByCond = UpcCall.queryOfferComChaByCond("P", productId, "RELATION_TYPE_CODE");//通过产品ID获取到RELATION_TYPE_CODE
		if(queryOfferComChaByCond.isEmpty()){
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "ProductId:"+productId+"没有配置相应的relationTypeCode");
			return true;
		}else{
			relationTypeCode = queryOfferComChaByCond.getData(0).getString("FIELD_VALUE");
		}
		IDataset allMeb = RelaUUInfoQry.getAllMebByUSERIDA(userIdA, relationTypeCode);//依据user_id_a/relation_type_code 查询所有成员
		if(allMeb.isEmpty()){
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "该集团下目前没有任何有效成员，暂不允许新增成员");
			return true;
		}
		return false;
	}

}
