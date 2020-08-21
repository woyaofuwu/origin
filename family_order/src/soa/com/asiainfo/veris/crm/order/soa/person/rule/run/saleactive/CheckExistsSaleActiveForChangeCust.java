package com.asiainfo.veris.crm.order.soa.person.rule.run.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;

public class CheckExistsSaleActiveForChangeCust  extends BreBase implements IBREScript{
	
	/**
	 * 核对用户是否办理了营销活动
	 * @param databus
	 * @param paramData
	 * @return
	 * @throws Exception
	 */
	public boolean run(IData databus, BreRuleParam paramData) throws Exception {
		
		String userId = databus.getString("USER_ID");
        IDataset saleActiveInfos = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
        if (IDataUtil.isNotEmpty(saleActiveInfos))
        {
        	String tips="原用户有生效的营销活动，过户后将转移至新用户，请告知客户此点并确认是否继续办理！";
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, "", tips);
        	return true;
        }
    return false;
		
		
	}
}
