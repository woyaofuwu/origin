package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:您已申请大客户备卡，该号码已办理优惠或者预约优惠，与USIM卡换卡业务存在互斥！
 * @author: xiaocl
 */


/*SELECT count(1) recordcount
FROM tf_b_trade_discnt a
WHERE a.trade_id = TO_NUMBER(:VTRADE_ID)
 AND a.accept_month = TO_NUMBER(:VACCEPT_MONTH)
 AND a.modify_tag = '0'
 AND EXISTS (Select 1
        From Td_s_Commpara b
       Where b.Subsys_Code = 'CSM'
         And b.Param_Attr = 8550
         And b.Param_Code = '4G'
         And b.Para_Code1 = a.Discnt_Code
         And Sysdate Between b.Start_Date And b.End_Date)
 AND EXISTS (SELECT 1
     FROM tf_f_user c
     WHERE c.user_id = a.user_id
     AND c.acct_tag = '0')
*/

public class is4GVipSimBak  extends BreBase implements IBREScript{
	private static Logger logger = Logger.getLogger(is4GVipSimBak.class);

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		if (logger.isDebugEnabled())
	        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in is4GVipSimBak() >>>>>>>>>>>>>>>>>>");

		boolean bResult = false;
		
		IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
		IDataset listUser = databus.getDataset("TF_F_USER");
		
		if(IDataUtil.isEmpty(listUser)) return false;
		if(!listUser.first().getString("ACCT_TAG").equals("0")) return false;
		
		String strEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
		
		IDataset list8550 = BreQryForCommparaOrTag.getCommpara("CSM", 8550,"4G", strEparchyCode);
		
		String strDiscntCode = "";
		for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
        {
            IData dataTradeDiscnt = (IData) iter.next();
            if("0".equals(dataTradeDiscnt.getString("MODIFY_TAG"))){
            	strDiscntCode = dataTradeDiscnt.getString("DISCNT_CODE") ;
            }
            for (int i = 0, iSize = list8550.size(); i < iSize; i++)
            {
            	IData data = list8550.getData(i);
            	if(StringUtils.equals(strDiscntCode, data.getString("PARA_CODE1"))){
            		bResult = true;
            	}
            }
            
        }
		if (logger.isDebugEnabled())
	        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out is4GVipSimBak() >>>>>>>>>>>>>>>>>>");
		return bResult;
	}

}
