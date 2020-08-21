package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class CheckDayEntityCardCNT extends BreBase implements IBREScript{
	
	/**
     * Copyright: Copyright 2016 Asiainfo
     * 
     * @Description: 
     * @author: 
     * 
     */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CheckDayEntityCardCNT.class);
	
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckDayEntityCardCNT() >>>>>>>>>>>>>>>>>>");

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
            	
            	 //REQ201708080007 关于流量实体卡办理次数控制的优化  start
            	IDataset  commparaByDiscntCode=CommparaInfoQry.getCommpara("CSM", "172", discntCode, "0898");
            	if(IDataUtil.isNotEmpty(commparaByDiscntCode)){
            		int limitCount = Integer.parseInt(commparaByDiscntCode.getData(0).getString("PARA_CODE1"));
            		String userDiscntMonthCount = UserDiscntInfoQry.getUserDiscntMonthCountNew(userId,discntCode);
            		int totalValue = Integer.parseInt(userDiscntMonthCount);
            		if(totalValue >= limitCount)
            		{
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20160328, "本月已订购该叠加卡"+ limitCount +"次，本月无法再订购！");
                    	bResult = true;
                    	break;
            		}
            	}
           	  //REQ201708080007 关于流量实体卡办理次数控制的优化  end	
            	/*if("440325".equals(discntCode))
            	{
            		String userDiscntMonthCount = UserDiscntInfoQry.getUserDiscntMonthCountNew(userId,discntCode);
            		int totalValue = Integer.parseInt(userDiscntMonthCount);
            		if(totalValue >= dealTimes)
            		{
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20160328, "本月已订购该叠加卡10次，本月无法再订购！");
                    	bResult = true;
                    	break;
            		}
            	}
            	
            	if("440326".equals(discntCode))
            	{
            		String userDiscntMonthCount = UserDiscntInfoQry.getUserDiscntMonthCountNew(userId,discntCode);
            		int totalValue = Integer.parseInt(userDiscntMonthCount);
            		if(totalValue >= dealTimes)
            		{
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20160328, "本月已订购该叠加卡10次，本月无法再订购！");
                    	bResult = true;
                    	break;
            		}
            	}
            	
            	 * 关于流量实体卡办理次数控制的优化
            	 * @author zhuoyingzhi
            	 * @date 20170803
            	
            	if("8739".equals(discntCode)||"8740".equals(discntCode))
            	{
            		String userDiscntMonthCount = UserDiscntInfoQry.getUserDiscntMonthCountNew(userId,discntCode);
            		int totalValue = Integer.parseInt(userDiscntMonthCount);
            		if(totalValue >= dealTimes)
            		{
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20160328, "本月已订购该叠加卡10次，本月无法再订购！");
                    	bResult = true;
                    	break;
            		}
            	}*/
            	
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckDayEntityCardCNT() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
