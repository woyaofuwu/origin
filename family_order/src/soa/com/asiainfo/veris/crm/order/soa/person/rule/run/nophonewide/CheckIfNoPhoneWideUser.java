
package com.asiainfo.veris.crm.order.soa.person.rule.run.nophonewide;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod.NoPhoneWideChangeProdBean;
 

/**
 *  根据录入的号码判断是否无手机宽带用户
 */
public class CheckIfNoPhoneWideUser extends BreBase implements IBREScript
{  
	private static Logger logger = Logger.getLogger(CheckIfNoPhoneWideUser.class);

	public boolean run(IData databus, BreRuleParam ruleparam) throws Exception
	{ 
		boolean tag=false;
		String sn=databus.getString("SERIAL_NUMBER");  
		IData inparam=new DataMap();
		
		if (sn.startsWith("KD_"))
		{  
			inparam.put("SERIAL_NUMBER",sn); 
    		IDataset userinfo=CSAppCall.call("SS.NoPhoneWideChangeProdSVC.checkIfNoPhoneWideUser", inparam);
    		if(userinfo!=null && userinfo.size()>0){
    			tag=true; 
    		} 
		}else{
			if(sn.length()==18){
				//根据身份证获取无手机宽带账号
				inparam.put("PSPT_ID",sn); 
				IDataset users= NoPhoneWideChangeProdBean.noPhoneUserQryByPSPTID(inparam);
		    	for(int i=0;i<users.size();i++){
		    		inparam.put("SERIAL_NUMBER",users.getData(i).getString("SERIAL_NUMBER","")); 
		    		IDataset userinfo=CSAppCall.call("SS.NoPhoneWideChangeProdSVC.checkIfNoPhoneWideUser", inparam);
		    		if(userinfo!=null && userinfo.size()>0){
		    			tag=true;
		    			break;
		    		}
		    	}
			}else{
				String kd_sn="KD_"+sn;
				inparam.put("SERIAL_NUMBER",kd_sn); 
	    		IDataset userinfo=CSAppCall.call("SS.NoPhoneWideChangeProdSVC.checkIfNoPhoneWideUser", inparam);
	    		if(userinfo!=null && userinfo.size()>0){
	    			tag=true; 
	    		}
			}
		}
		
		
		if (tag==false)
		{
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2017011201, "该号码非无手机宽带用户，无法办理该业务。");
			return false;
		}

		if (logger.isDebugEnabled())
		{
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckIfNoPhoneWideUser() >>>>>>>>>>>>>>>>>>");
		}

		return true;
	}
}
