package com.asiainfo.veris.crm.order.soa.script.rule.res;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class CheckExistStopTopsetboxRule extends BreBase implements IBREScript{

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		String serialNumber = databus.getString("SERIAL_NUMBER");
    	
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	if(IDataUtil.isNotEmpty(userInfo)){
    		String userId = userInfo.getString("USER_ID");
        	
        	IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J");
        	
        	if(IDataUtil.isNotEmpty(boxInfos)){
        		
        		IData boxInfo=boxInfos.getData(0);
        		
        		
        		//获取产品
        		String basePackageInfo=boxInfo.getString("RSRV_STR2","");
        		if(!basePackageInfo.equals("")){
        			String serviceId=basePackageInfo.split(",")[0];
        			
        			IDataset userPlatSvcs=UserPlatSvcInfoQry.queryUserPlatSvcByState(userId, serviceId, "N");
        			if(IDataUtil.isNotEmpty(userPlatSvcs)){
        				if(boxInfo.getString("RSRV_TAG3","").equals("1")){		//1是前台办理的停机
                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", "用户魔百和业务已经被报停，请进行魔百和报开以后再进行操作！");
                			
                		}else{
                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", "用户魔百和业务已经被欠费报停，请进行充值报开以后再进行操作！");
                		}
        				
        			}
        		}

        	}
        	
    	}
    	
    	
    	return false;
    	
	}
	
}
