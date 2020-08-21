package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;


public class CheckDayGprsDiscnt extends BreBase implements IBREScript
{

	/**
     * Copyright: Copyright 2016 Asiainfo
     * 
     * @Description: 流量日包的受理判断 【CheckDayGprsDiscnt】
     * @author: songlm
     * 1、每月最后一天不允许办理；2、每月最多办理10个
     */
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CheckDayGprsDiscnt.class);
    
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckDayGprsDiscnt() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String userId = databus.getString("USER_ID");
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");//获取优惠子台帐
        
        //循环优惠子台帐
        for (int i = 0, count = listTradeDiscnt.size(); i < count; i++)
        {
        	IData tradeDiscnt = listTradeDiscnt.getData(i);
            String modifyTag = tradeDiscnt.getString("MODIFY_TAG");
            
            //只有优惠子台帐为新增类型的时候才进
            if("0".equals(modifyTag))
            {
            	String discntCode = tradeDiscnt.getString("DISCNT_CODE");
            	//是新增日流量包优惠，才进
            	if("20160322".equals(discntCode) || "20160323".equals(discntCode) || "20161323".equals(discntCode) || "20161322".equals(discntCode))
            	{
            		/* REQ201701180008 关于流量冲浪包优化的需求
            		 * CRM侧放开月底最后一天办理流量冲浪小时包与流量冲浪日包的限制
            		 * Modify by zhangxing3 on 2017-02-06
            		//1、最后一天不允许办理
            		String LastDayThisMonth = SysDateMgr.getLastCycleThisMonth();//得到本月最后一天，格式YYYYMMDD
            		String tradeDiscntStartDate = tradeDiscnt.getString("START_DATE");//采用优惠的开始时间作为判断，这样才能保证不给计费账务同步最后一天的优惠数据
            		String startDate = SysDateMgr.date2String(SysDateMgr.string2Date(tradeDiscntStartDate, "yyyy-MM-dd"),"yyyyMMdd");//格式转换为YYYYMMDD
            		if(startDate.equals(LastDayThisMonth))
            		{
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2016032501, "本月最后一天，不允许订购日流量包！");
                    	bResult = true;
                    	break;
            		}
            		*/
            		
            		//2、用户含有10个了，不允许办理
            		String userDiscntMonthCount = UserDiscntInfoQry.getUserDiscntMonthCount(userId);
            		int totalValue = Integer.parseInt(userDiscntMonthCount);
            		if(totalValue >= 10)
            		{
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2016032502, "本月已订购10次流量日包业务，本月无法再订购！");
                    	bResult = true;
                    	break;
            		}
            	}
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckDayGprsDiscnt() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
