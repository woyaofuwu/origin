package com.asiainfo.veris.crm.order.soa.person.rule.run.discnt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class CheckUserOpenDiscntOne extends BreBase implements IBREScript 
{

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
		IDataset listTradeDiscnts = databus.getDataset("TF_B_TRADE_DISCNT");
		
		if(IDataUtil.isNotEmpty(listTradeDiscnts))
		{
			for(int i=0, size=listTradeDiscnts.size(); i<size; i++)
			{
				IData listTradeDiscnt = listTradeDiscnts.getData(i);
				String strModifyTag = listTradeDiscnt.getString("MODIFY_TAG");
				if(BofConst.MODIFY_TAG_ADD.equals(strModifyTag))
				{
					String strUserId = listTradeDiscnt.getString("USER_ID");
					String discntCode = listTradeDiscnt.getString("DISCNT_CODE");
					
					//产品变更界面，限制此优惠只能办理一次
					IDataset checkDats = CommparaInfoQry.getCommparaInfoByCode("CSM", "1500", "OPEN_DISCNT_ONE", discntCode, "0898");
					
					if(IDataUtil.isNotEmpty(checkDats))
					{
						 IDataset userDiscs = UserDiscntInfoQry.getAllDiscntByUser_2(strUserId, discntCode);
	                     if (userDiscs != null && userDiscs.size() > 0) {
	                    	 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 150001, "你已办理过"+checkDats.first().getString("PARAM_NAME")+"，不能再次办理该"+checkDats.first().getString("PARAM_NAME")+"优惠包！");
	                         return true;
	                     }
					}
					
					//产品变更界面，两个产品互斥
					IDataset checkDatsMutex = CommparaInfoQry.getCommparaInfoByCode("CSM", "1500", "OPEN_DISCNT_MUTEX", discntCode, "0898");
					
					if(IDataUtil.isNotEmpty(checkDatsMutex))
					{
	                     String checkDatsdiscntCodeMutex = checkDatsMutex.first().getString("PARA_CODE3");
	                     IDataset userDiscs1 = UserDiscntInfoQry.getAllDiscntByUser_2(strUserId, checkDatsdiscntCodeMutex);
	                     if (userDiscs1 != null && userDiscs1.size() > 0) {
	                    	 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 150001, "你已办理过"+checkDatsMutex.first().getString("PARA_CODE4")+"，不能再次办理该"+checkDatsMutex.first().getString("PARAM_NAME")+"优惠包！");
	                         return true;
	                     }
					}
				}
			}
		}
		return false;
    }
	
}
