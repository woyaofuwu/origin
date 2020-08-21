package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class ChkWlwMebOrderByGrpTrade extends BreBase implements IBREScript
{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(ChkWlwMebOrderByGrpTrade.class);
    
	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkWlwMebOrderByGrpTrade()  >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = true;
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userId = databus.getString("USER_ID");
        String productId = databus.getString("PRODUCT_ID");
        IDataset tradeInfos = TradeInfoQry.getTradeByUserIdProductId(userId, productId);
        if(IDataUtil.isNotEmpty(tradeInfos))
        {
        	err = "该集团有未完工物联网工单，不能办理物联网成员业务！";

            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            //bResult = false;
        }
        

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出ChkWlwMebOrderByGrpTrade() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
	}

}
