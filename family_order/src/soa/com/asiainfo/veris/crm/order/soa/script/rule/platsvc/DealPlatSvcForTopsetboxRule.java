package com.asiainfo.veris.crm.order.soa.script.rule.platsvc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class DealPlatSvcForTopsetboxRule extends BreBase implements IBREScript{

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		String inModeCode=databus.getString("IN_MODE_CODE");
//		inModeCode="5";
		
		//只是针对短厅渠道
		if(inModeCode.equals("5")){
			String serviceId=databus.getString("SERVICE_ID");
			String operCode=databus.getString("OPER_CODE");
			
			//如果是取消服务，需要验证服务是否是互联网电视的服务，如果是，就是不允许在接口进行取消
			if(operCode.equals("02")){
				//判断用户是否魔百和客户
				String userId=databus.getString("USER_ID");
				
				IDataset userPlatSvcs=UserPlatSvcInfoQry.queryUserPlatSvcByUserIdAndServiceId(userId, serviceId);
				if(IDataUtil.isNotEmpty(userPlatSvcs)){
					IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
					if(IDataUtil.isNotEmpty(boxInfos)){
						IDataset platSvcs=PlatSvcInfoQry.queryBizServiceByBizTypeCode(serviceId, "51");
						if(IDataUtil.isNotEmpty(platSvcs)){
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", "用户属于魔百和用户，无法取消用户魔百和相关服务！");
							return true;
						}
					}
				}
			}
		}
		
		
		return false;
	}
}
