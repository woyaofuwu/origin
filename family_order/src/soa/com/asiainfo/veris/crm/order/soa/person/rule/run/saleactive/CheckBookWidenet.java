
package com.asiainfo.veris.crm.order.soa.person.rule.run.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckBookWidenet.java
 * @Description: 宽带1+营销活动受理
 * @version: v1.0.0
 * @author: 
 * @date: 2014-5-20 下午4:22:20
 */
public class CheckBookWidenet extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
      String userId = databus.getString("USER_ID");

      String productId = databus.getString("PRODUCT_ID", "");
      String packageId = databus.getString("PACKAGE_ID", "");
      
      IDataset hdfkActives2 = SaleActiveInfoQry.queryHdfkActivesByUserId2(userId);
      if (IDataUtil.isNotEmpty(hdfkActives2))
      {
      	for(int i=0;i<hdfkActives2.size();i++){
      		IData bookActive = hdfkActives2.getData(i);
      		if(StringUtils.equals(productId, bookActive.getString("PRODUCT_ID"))){
	         		String tradeId = bookActive.getString("ACCEPT_TRADE_ID");
	         		IDataset tradeInfos = TradeInfoQry.getMainTradeByTradeId(tradeId);
	         		if(IDataUtil.isNotEmpty(tradeInfos)){
	         			String tradeTypeCode = tradeInfos.getData(0).getString("TRADE_TYPE_CODE");
	              		if(StringUtils.equals(tradeTypeCode, "600") || StringUtils.equals(tradeTypeCode, "611") || StringUtils.equals(tradeTypeCode, "612") ||StringUtils.equals(tradeTypeCode, "613")  )
	              		{
	                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 15014023, "用户存在预受理的宽带1+营销活动，业务办理不能继续！");
//	                        return false;
	              		}
	//                   BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "", "用户存在预受理的宽带1+营销活动，业务办理不能继续！");
	         		}
      	}
      	}
      }
//      
//      IDataset hdfkActives2 = SaleActiveInfoQry.queryHdfkActivesByUserId2(userId);
//      if (IDataUtil.isNotEmpty(hdfkActives2))
//      {
//      	for(int i=0;i<hdfkActives2.size();i++){
//      		IData bookActive = hdfkActives2.getData(i);
//      		String tradeId = bookActive.getString("ACCEPT_TRADE_ID");
//      		IDataset tradeInfos = TradeInfoQry.getMainTradeByTradeId(tradeId);
//      		String tradeTypeCode = tradeInfos.getData(0).getString("TRADE_TYPE_CODE");
//      		if(StringUtils.equals(tradeTypeCode, "600") || StringUtils.equals(tradeTypeCode, "600") ||StringUtils.equals(tradeTypeCode, "600")  )
//      		if(IDataUtil.isNotEmpty(tradeInfos)){
//                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "", "用户存在预受理的宽带1+营销活动，业务办理不能继续！");
//      		}
//      	}
//      }

        return true;
    }
}
