package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;

/**
 * 检验集团是否是物联网EC白名单并且物联网主体产品是审核通过的
 * 
 * @author yuanz
 * 
 */
public class CheckWLWGrpOrderECWhiteLimit extends BreBase implements IBREScript {

	private static final Logger logger = Logger
			.getLogger(CheckWLWGrpCreateCustLimit.class);

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckWLWGrpOrderECWhiteLimit() databus>>>>>>>>>>>>>>>>>>"
					+ databus);
		}
		String errCode = databus.getString("RULE_BIZ_ID");
		String brandCode = databus.getString("BRAND_CODE");
		if ("WLWG".equals(brandCode)) {
			String productId = databus.getString("PRODUCT_ID");
			IData param = new DataMap();
			String custId = databus.getString("CUST_ID");
			IDataset grpSet = GrpInfoQry.queryGrpIdBygrpcustId(custId);
			if (grpSet != null && grpSet.size() > 0) {
				IData reData = grpSet.getData(0);
				param.put("GROUP_ID", reData.getString("GROUP_ID"));
			} else {
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,
						errCode, "该集团办理物联网产品订购时，集团资料有误！");
				return true;
			}
			IDataset reDataset = GrpInfoQry.queryGrpWLWECWhiteBygrpId(param);
			if (reDataset != null && reDataset.size() > 0) {
				IData reData = reDataset.getData(0);
				String products = reData.getString("M_PRODUCT", "");
				boolean conflag = products.contains(productId);
				if (!conflag) {
					BreTipsHelp.addNorTipsInfo(databus,
							BreFactory.TIPS_TYPE_ERROR, errCode,
							"订购的主体产品不在审核通过范围内，不允许发起订购操作!");
					return true;
				}
			} else {
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,
						errCode, "该集团还不是物联网EC白名单，不允许发起订购操作!");
				return true;
			}
			return false;
		}
		return false;
	}

}
