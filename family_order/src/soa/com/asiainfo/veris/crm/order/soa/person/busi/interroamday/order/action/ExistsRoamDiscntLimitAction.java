package com.asiainfo.veris.crm.order.soa.person.busi.interroamday.order.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class ExistsRoamDiscntLimitAction implements ITradeAction
{
	private static transient Logger logger = Logger.getLogger(ExistsRoamDiscntLimitAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>> 进入ExistsRoamDiscntLimitAction() >>>>>>>>>>>>>>>");

        //1.获取国漫产品配置
        IDataset commparaSet =  CommparaInfoQry.getCommByParaAttr("CSM", "2742", "ZZZZ");
        if (IDataUtil.isEmpty(commparaSet))
        {
        	return ;
        }
//        String strDiscntCode = ruleParam.getString(databus, "DISCNT_CODE");
//        if (logger.isDebugEnabled())
//            logger.debug(" >>>>>>>> 进入ExistsRoamDiscntLimit() >>>>>>strDiscntCode="+strDiscntCode);
        
        
        //2.获取本次操作的资费列表
//        IDataset selectElements = new DatasetList(databus.getString("DISCNT_LIST","[]"));
        List<DiscntTradeData> selectElements = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
	    
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入ExistsRoamDiscntLimitAction() >>>>>>>>>>>>>>>>>>selectElements="+selectElements);
        
        if(/*StringUtils.isEmpty(strDiscntCode) && */DataUtils.isEmpty(selectElements))
        {
        	return ;
        }
        
        //3.判断为不可在当前界面受理国漫产品
        for(int i=0;i<selectElements.size();i++)
        {
        	DiscntTradeData  element = selectElements.get(i);
        	String elementId = element.getElementId();
        	for(int j=0;j<commparaSet.size();j++)
        	{
        		IData commParam = commparaSet.getData(j);
        		if(elementId.equals(commParam.getString("PARAM_CODE")))
        		{
        			CSAppException.apperr(CrmCommException.CRM_COMM_103,"业务受理前条件判断：国漫相关产品，请前往“国漫数据流量定向套餐业务”界面受理！");
//        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 194201, "业务受理前条件判断：国漫相关产品，请前往“国漫数据流量定向套餐业务”界面受理！");
        		}
        	}
        }
        
        if (logger.isDebugEnabled())
//            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckBeforeStart() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return ;
	}

}
