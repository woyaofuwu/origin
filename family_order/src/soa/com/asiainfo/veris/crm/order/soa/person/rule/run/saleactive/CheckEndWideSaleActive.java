package com.asiainfo.veris.crm.order.soa.person.rule.run.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;

/**
 * 校验用户是否在产品变更时选择终止宽带1+
 *  提交提示
 * @author lizj
 * @date 2019-10-11
 */
public class CheckEndWideSaleActive extends BreBase implements IBREScript{

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {

		String serialNumber = databus.getString("SERIAL_NUMBER");
		String xChoiceTag = databus.getString("X_CHOICE_TAG");
		String pageSouce = databus.getString("RSRV_STR9","");//判断是否前台调用
		System.out.println("进入CheckEndWideSaleActive"+serialNumber+"!!"+databus+"??"+ruleParam);
	    if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
	      if(!"N".equals(pageSouce)){
	    	  IDataset tradeDatas = TradeInfoQry.getMainTradeBySN(serialNumber, "110");
		      if(IDataUtil.isNotEmpty(tradeDatas)){
		    	  String rsrvStr10 = tradeDatas.first().getString("RSRV_STR10","");
		    	  String tradeId = tradeDatas.first().getString("TRADE_ID");
		    	  if("Y".equals(rsrvStr10)||"N".equals(rsrvStr10)){
		    		  IDataset productTrades = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
		    		  if (IDataUtil.isNotEmpty(productTrades))
		    	        {
		    			  String offerCode = productTrades.first().getString("PRODUCT_ID");
		    			  IDataset commparaInfos9221 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9921",offerCode,null);
		    			  if(IDataUtil.isNotEmpty(commparaInfos9221)){
		    				  BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20200219", "在产品变更时选择终止宽带1+活动后不能再终止宽带1+！");
							  return true;
		    			  }
		    	        }
		    	  }
		    	 
		      }
	    	  
	      }

        }
		
		 
		return false;
	}

}
