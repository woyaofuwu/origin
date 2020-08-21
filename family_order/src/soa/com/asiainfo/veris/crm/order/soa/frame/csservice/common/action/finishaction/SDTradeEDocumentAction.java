
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;

public class SDTradeEDocumentAction implements ITradeFinishAction
{

    /**
     * 新大陆调用服务生成电子工单
     * 
     * @param 
     * @auth linsl
     */
	 public void executeAction(IData mainTrade) throws Exception
	    {
		 	String inModeCode = mainTrade.getString("IN_MODE_CODE");
		 	String trade_type_code=mainTrade.getString("TRADE_TYPE_CODE");
		 	String prt_tag ="";
		 	String eparchCode;
	        if ("1".equals(CSBizBean.getVisit().getInModeCode()))
	        {
	            eparchCode = BizRoute.getRouteId();
	        }
	        else
	        {
	            eparchCode = CSBizBean.getTradeEparchyCode();
	        }
		 	IData tradeType = UTradeTypeInfoQry.getTradeType(mainTrade.getString("TRADE_TYPE_CODE"), eparchCode);
		 	prt_tag=tradeType.getString("PRT_TRADEFF_TAG");
		 	
		 	//智能终端宽带开户可能生成多笔230营销活动预受理工单，230同时完工调用打印会有主键冲突问题，所以屏蔽230/600打印， 600工单在App查询电子工单时生成
		 	if("SD".equals(inModeCode) && "1".equals(prt_tag) && !"6800".equals(trade_type_code) && !"600".equals(trade_type_code) && !"230".equals(trade_type_code))
		 	{
		 		CSAppCall.call("CS.PrintNoteSVC.getPrintData", mainTrade);
		 	}   
	    }
}
