package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * 办理以下TD产品的客户，不能办理宽带业务
 * 
 * @author 
 */
public class CheckWidenetUserProductForTDProduct extends BreBase implements IBREScript {

	private static final long serialVersionUID = 1L;

	public boolean run(IData databus, BreRuleParam param) throws Exception {
		String xChoiceTag = databus.getString("X_CHOICE_TAG");
		if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
		{
			String userId = databus.getString("USER_ID");
			IDataset userInfos = UserProductInfoQry.getUserTDProductByUserId(userId);
			if (IDataUtil.isNotEmpty(userInfos)) {
				String discntName = userInfos.getData(0).getString("PARAM_NAME","");
				String productId = userInfos.getData(0).getString("PRODUCT_ID","");
				String errorMsg = "该用户已经订购了" + discntName + "产品[" + productId + "] ，业务不能继续！";
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,"201504071617010", errorMsg);
				return true;
			}
		}
		return false;
	}
}
