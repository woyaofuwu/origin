package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * 办理和目尝鲜活动不允许宽带拆机。
 * @author Administrator
 *
 */
public class CheckHMBaoRule extends BreBase implements IBREScript{

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		String sn=databus.getString("SERIAL_NUMBER");
		if(StringUtils.isNotEmpty(sn)){
			if(sn.contains("KD_")){
				sn=sn.replaceAll("KD_", "");
			}
			IData userData=UcaInfoQry.qryUserInfoBySn(sn);
			if(userData!=null){
				//用户办理了和目营销活动后，即使活动已经结束（自然结束或者提前终止），再去办理宽带拆机时会拦截，提示用户有和目尝鲜活动拦截。改造点：在宽带拆机界面，把拦截在网时间的判断条件改为判断和目的活动结束时间。wangsc10-20190314
				IDataset saleActives=UserSaleActiveInfoQry.querySaleActiveByUserIdProcess(userData.getString("USER_ID"),"0");
				
				IDataset productIds=CommparaInfoQry.getCommparaAllColByParser("CSM", "8987", null, "0898");
				
				if(IDataUtil.isNotEmpty(saleActives)){
					for(int i=0;i<saleActives.size();i++){
						
						IData saleData=saleActives.getData(i);
							
						if(IDataUtil.isNotEmpty(productIds)){
							for(int j=0;j<productIds.size();j++){
								if(saleData.getString("PRODUCT_ID","").equals(productIds.getData(j).get("PARAM_CODE"))
										&&saleData.getString("PACKAGE_ID","").equals(productIds.getData(j).get("PARA_CODE1"))){
									BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "701010", "用户办理了和目尝鲜活动["+saleData.getString("PRODUCT_ID")+"]包["+saleData.getString("PACKAGE_ID")+"],不允许办理宽带拆机!");
									return true;
								}
							}
						}
					}
				}
			}
		}
		
		return false;
	}

}
