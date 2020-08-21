package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;


public class CheckTradePDAttr538 extends BreBase implements IBREScript
{

	/**
     * Copyright: Copyright 2016 Asiainfo
     * 
     * @Description: 
     * @author: yanwu
     * 
     */
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CheckTradePDAttr538.class);
    
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckDayGprsDiscnt() >>>>>>>>>>>>>>>>>>");
        }
 
        boolean bResult = false;
        //限制538 TD_S_COMMPARA配置编码 
        IDataset CommparaAttr538 = CommparaInfoQry.getCommByParaAttr("CSM", "538", "0898");
        if(IDataUtil.isEmpty(CommparaAttr538))
		{
        	return bResult;
		}
        
        for (int i = 0; i < CommparaAttr538.size(); i++) 	
        {
        	
        	IData CommparaPara = CommparaAttr538.getData(i);
			String strParaCode1 = CommparaPara.getString("PARA_CODE1", "");
			String strParaCode2 = CommparaPara.getString("PARA_CODE2", "");
			String strParaCode24 = CommparaPara.getString("PARA_CODE24", "");
			String strParamCode = CommparaPara.getString("PARAM_CODE", "");
			String strParaCode3 = CommparaPara.getString("PARA_CODE3", "0");
			if("P".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1) && "1".equals(strParaCode2))
			{
				
				IDataset listTradeProduct = databus.getDataset("TF_B_TRADE_PRODUCT");	//获取产品子台帐
				if(IDataUtil.isNotEmpty(listTradeProduct))
				{
					//循环产品子台帐
					for (int j = 0, count = listTradeProduct.size(); j < count; j++)
			        {
						IData tradeProduct = listTradeProduct.getData(j);
						String modifyTag = tradeProduct.getString("MODIFY_TAG");
						//只有产品子台帐为新增类型的时候才进
			            if(strParaCode3.equals(modifyTag))
			            {
			            	String discntCode = tradeProduct.getString("PRODUCT_ID");
			            	if(discntCode.equals(strParaCode1))
			            	{
			            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2016070801, strParaCode24);
		                    	bResult = true;
		                    	break;
			            	}
			            }
			        }
				}
				
			}
			else if("D".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1) && "1".equals(strParaCode2))
			{
				
				IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");		//获取优惠子台帐
				if(IDataUtil.isNotEmpty(listTradeDiscnt))
				{
					//循环优惠子台帐
					for (int j = 0, count = listTradeDiscnt.size(); j < count; j++)
			        {
						IData tradeDiscnt = listTradeDiscnt.getData(j);
			            String modifyTag = tradeDiscnt.getString("MODIFY_TAG");
			            //只有优惠子台帐为新增类型的时候才进
			            if(strParaCode3.equals(modifyTag))
			            {
			            	String discntCode = tradeDiscnt.getString("DISCNT_CODE");
			            	if(discntCode.equals(strParaCode1))
			            	{
			            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2016070802, strParaCode24);
		                    	bResult = true;
		                    	break;
			            	}
			            }
			        }
				}
				
			}
			
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckDayGprsDiscnt() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        }

        return bResult;
    }
}
