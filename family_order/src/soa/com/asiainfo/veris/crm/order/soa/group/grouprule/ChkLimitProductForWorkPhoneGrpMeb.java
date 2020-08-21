package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class ChkLimitProductForWorkPhoneGrpMeb extends BreBase implements IBREScript
{	
	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkLimitProductForWorkPhoneGrpMeb.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
		if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkLimitProductForWorkPhoneGrpMeb() >>>>>>>>>>>>>>>>>>");
        }

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */
        String userIdB = databus.getString("USER_ID_B", "");//成员用户UserId
        
        if(StringUtils.isNotBlank(userIdB))
        {
        	IDataset productInfos = UserProductInfoQry.queryWorkPhoneGrpMebByUserId(userIdB);
        	if(IDataUtil.isNotEmpty(productInfos))
        	{   
        		String productId = productInfos.getData(0).getString("PRODUCT_ID","");
        		String productName = UProductInfoQry.getProductNameByProductId(productId);
        		err = "号码订购了如下产品[" + productName + "]产品ID[" + productId + "],限制加入!";
    			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
    			return false;
        	}
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ChkLimitProductForWorkPhoneGrpMeb()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
	}

}
