package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class CheckMoveWidenetType extends BreBase implements IBREScript {
	private static final long serialVersionUID = 1L;

	public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        IDataset listTrade = databus.getDataset("TF_B_TRADE");
        IDataset listTradeWidenet = databus.getDataset("TF_B_TRADE_WIDENET");
        String tradeWidenet01 = "";
        String tradeWidenet02 = "";
        String intfId = "";
        if(IDataUtil.isNotEmpty(listTrade) && listTrade.size()> 0){
        	intfId = listTrade.getData(0).getString("INTF_ID");
        }
        
        for (Iterator iter = listTradeWidenet.iterator(); iter.hasNext();)
        {
            IData tradeWidenet = (IData) iter.next();
            String widenetType = tradeWidenet.getString("RSRV_STR2","");
            String modifyTag = tradeWidenet.getString("MODIFY_TAG","");
            if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
            	tradeWidenet01 = widenetType;
            }else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
            	tradeWidenet02 = widenetType;
            }
        }
        
        //如果跨制式移机，有做产品变更不？
        /*if(!tradeWidenet01.equals(tradeWidenet02)){
        	if(intfId.indexOf("TF_B_TRADE_PRODUCT")<0){
        		String errorInfo = "跨制式移机" + tradeWidenet02 + "》" + tradeWidenet02 + "，请移机同时做产品变更，以便套餐符合制式！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20170217001", errorInfo);
                return true;
        	}
        }*/
        
        if(!tradeWidenet01.equals(tradeWidenet02))
        {
        	//1、6都用到FTTB产品，3、5用到是FTTH产品，不需要做产品变更
        	if (("1".equals(tradeWidenet01) && "6".equals(tradeWidenet02)) 
        			|| ("6".equals(tradeWidenet01) && "1".equals(tradeWidenet02))
        			|| ("3".equals(tradeWidenet01) && "5".equals(tradeWidenet02))
        			|| ("5".equals(tradeWidenet01) && "3".equals(tradeWidenet02)))
        	{
        		return false;
        	}
        	else
        	{
        		if(intfId.indexOf("TF_B_TRADE_PRODUCT")<0)
            	{
            		String errorInfo = "跨制式移机" + tradeWidenet01 + "》" + tradeWidenet02 + "，请移机同时做产品变更，以便套餐符合制式！";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20170217001", errorInfo);
                    return true;
            	}
        	}
        	
        	
        }
        
        
		return false;
    }

}
