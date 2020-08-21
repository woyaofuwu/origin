
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeProductLimit.java
 * @Description: REQ201901070029新增移动花卡--更换主套餐的限制；
				 用户花卡套餐生效第一个完整自然月起，不允许用户更换主套餐 
				   
 * @version: v1.0.0
 * @author: tanzheng
 * @date: 2019-1-9
 */
public class ChangeProductLimit extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ChangeProductLimit.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChangeProductLimit() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        //如果工号有权限就直接返回
        if( StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "ALLOW_CHANGE_HUAKA_PRODUCT")){
        	logger.debug("工号"+getVisit().getStaffId()+"有ALLOW_CHANGE_HUAKA_PRODUCT 权限");
        	return bResult;
        }
       
        
        
		IDataset listTradeproduct = databus.getDataset("TF_B_TRADE_PRODUCT");
		//如果有主产品变更
		if(IDataUtil.isNotEmpty(listTradeproduct)){

			IDataset comparas =BreQryForCommparaOrTag.getCommpara("CSM",2578,"ZZZZ");
			for(Object temp : comparas){
				IData tempData = (IData) temp;
				for(Object tempPro : listTradeproduct){
					IData tempProData = (IData) tempPro;
					if("1".equals(tempProData.getString("MODIFY_TAG")) && tempData.getString("PARA_CODE1").equals(tempProData.getString("PRODUCT_ID"))){
						 String proStartDate = tempProData.getString("START_DATE");
						 System.out.println("aaa");
						 //
						 String limitDate = SysDateMgr.getAddMonthsLastDay(tempData.getInt("PARA_CODE2"), proStartDate);
						 
						 
						 String nowDate = SysDateMgr.date2String(new Date(),SysDateMgr.PATTERN_STAND);
						 if (SysDateMgr.compareTo(nowDate,limitDate) < 0)
						 {
							 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2019010901, "该号码办理了"+tempData.getString("PARA_CODE3")+"要求"+tempData.getString("PARA_CODE2")+"月内在不能变更主产品！");
							 bResult = true;
							 break;
						 }
					}
				}
			}

		}       
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChangeProductLimit() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
