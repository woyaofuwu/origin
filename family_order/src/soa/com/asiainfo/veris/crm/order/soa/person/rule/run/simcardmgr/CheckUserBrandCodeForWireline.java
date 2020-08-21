package com.asiainfo.veris.crm.order.soa.person.rule.run.simcardmgr;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
/**
 * 校验无线固话和商务电话补换卡不能在【换卡写卡界面办理】
 * @author Administrator
 *
 */
public class CheckUserBrandCodeForWireline extends BreBase implements IBREScript{

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		String xChoiceTag = databus.getString("X_CHOICE_TAG");
		if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag)){
			//UcaData uca = (UcaData) databus.get("UCADATA");
			String serialNumber = databus.getString("SERIAL_NUMBER");
			UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		    String brandCode=uca.getBrandCode();
			if("TDYD".equals(brandCode)){
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2018052301, "该用户是TD无线座机，请到无线固话换卡界面办理");
                return true;
			}
			if("simcardmgr.RemoteCard".equals(databus.getString("page")) 
					&&("TT02".equals(brandCode)||"TT04".equals(brandCode))){
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2018052301, "该用户是TD一代固话，请到铁通系统办理换卡");
                return true;
			}
		}
		return false;
	}
}
