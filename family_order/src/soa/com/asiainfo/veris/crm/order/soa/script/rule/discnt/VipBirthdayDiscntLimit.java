package com.asiainfo.veris.crm.order.soa.script.rule.discnt;



import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 
 * @author: 
 */
public class VipBirthdayDiscntLimit extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(VipBirthdayDiscntLimit.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        boolean bResult = false;
        String start_date = "";
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
        	IData reqData = databus.getData("REQDATA");// 请求的数据
        	if (IDataUtil.isNotEmpty(reqData)){
		        if (logger.isDebugEnabled())
		            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 VipBirthdayDiscntLimit() >>>>>>>>>>>>>>>>>>");
		       
		        String strDiscntCode = ruleParam.getString(databus, "DISCNT_CODE");//获取大客户畅打优惠
		        String strProductId = ruleParam.getString(databus, "PRODUCT_ID");//获取和生日送电子券营销活动的活动product_id
		        IDataset  select_elements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
		        if(IDataUtil.isNotEmpty(select_elements)){
		        String limtFlag = "0";
		        for (int i = 0, size = select_elements.size(); i < size; i++)
                {
                    IData element = select_elements.getData(i);

                    String tempElementId = element.getString("ELEMENT_ID");
                    String modifyTag = element.getString("MODIFY_TAG");
                    if(strDiscntCode.equals(tempElementId)&&"1".equals(modifyTag)){
                    	start_date = element.getString("START_DATE");
                    	String StartDate = SysDateMgr.getDateForSTANDYYYYMMDD(start_date);
                    	String SysDate = SysDateMgr.getDateForSTANDYYYYMMDD(SysDateMgr.getSysTime());
                    	if(SysDate.compareTo(StartDate)>0){
                    		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", "该优惠生效后不能取消");
    	                    return true;
                    	}
                    }
		            if(strDiscntCode.equals(tempElementId)&&"0".equals(modifyTag)){
		            	start_date = element.getString("START_DATE");
		            	limtFlag="1";
		            	break;
		            }
                }
				if ("1".equals(limtFlag)) {
					//办理的时候才需要校验
					UcaData uca = (UcaData) databus.get("UCADATA");
					IData data = new DataMap();
					data.put("USER_ID", uca.getUser().getUserId());
					data.put("DISCNT_CODE", strDiscntCode);
					data.put("BOOK_DATE",start_date);
					IDataset listUserDiscnt = UserDiscntInfoQry.getVipBirthDiscnt(data);
					if (IDataUtil.isNotEmpty(listUserDiscnt)){
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", "该优惠一年只能办理一次");
	                    return true;
					}
					String credit_class = uca.getUserCreditClass();
					logger.debug("=================="+credit_class+"========="+credit_class.compareTo("5"));
					if (credit_class.compareTo("5")< 0) {
		                  //5星级及5星级以上用户才可办理此优惠
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", "5星级及5星级以上用户才可办理此优惠");
		                return true;
					} 
					IData param = new DataMap();
					param.put("USER_ID", uca.getUser().getUserId());
					param.put("PRODUCT_ID",strProductId);
					IDataset listUserSaleActive = UserSaleActiveInfoQry.queryVipBirthSaleActive(param);
					if (IDataUtil.isNotEmpty(listUserSaleActive)){
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", "该优惠与和生日送电子券营销活动互斥");
	                    return true;
					}
				} else {
					//不需要校验,直接返回
					return bResult;
				}
		        }
        	}
        }
        return bResult;
    }
}

