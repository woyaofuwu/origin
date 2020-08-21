
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DestroyAccountLimit.java
 * @Description: REQ201901070029新增移动花卡--更换主套餐的限制；
				 用户花卡套餐生效第一个完整自然月起，12个月内不允许销户 
				   
 * @version: v1.0.0
 * @author: tanzheng
 * @date: 2019-1-9
 */
public class DestroyAccountLimit extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(DestroyAccountLimit.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 DestroyAccountLimit() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String userId = databus.getString("USER_ID", "0");
		IData userMainProductInfo = UcaInfoQry.qryMainProdInfoByUserId(userId);
	    if (IDataUtil.isEmpty(userMainProductInfo))
	    {
	         CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户主产品信息表时出错");
	    }
		IDataset comparas =BreQryForCommparaOrTag.getCommpara("CSM",2578,"ZZZZ");
		logger.debug("  DestroyAccountLimit() >>>>>>>>>>>>userMainProductInfo"+userMainProductInfo.toString());
		logger.debug("  DestroyAccountLimit() >>>>>>>>>>>>comparas"+comparas.toString());
		for(Object temp : comparas){
				IData tempData = (IData) temp;
				if(tempData.getString("PARA_CODE1").equals(userMainProductInfo.getString("PRODUCT_ID"))){
					 String proStartDate = userMainProductInfo.getString("START_DATE");
					 int monthInterval = tempData.getInt("PARA_CODE2");
					 if(monthInterval==0){
						 continue;
					 }
					 String limitDate = SysDateMgr.getAddMonthsLastDay(tempData.getInt("PARA_CODE2"), proStartDate);
					 String nowDate = SysDateMgr.date2String(new Date(),SysDateMgr.PATTERN_STAND);
					 logger.debug(" >>>>>>>DestroyAccountLimit() START_DATE limitDate nowDate>>>>>>>>>>>>> " +proStartDate+ "=======" +limitDate + "=======" +nowDate);
					 if (SysDateMgr.compareTo(nowDate,limitDate) < 0)
					 {
						 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2019010902, "该号码办理了"+userMainProductInfo.getString("PRODUCT_NAME", "")+"要求"+tempData.getString("PARA_CODE2")+"月内在不能销户！");
						 bResult = true;
						 break;
					 }
				}
		}
     
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出DestroyAccountLimit() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
