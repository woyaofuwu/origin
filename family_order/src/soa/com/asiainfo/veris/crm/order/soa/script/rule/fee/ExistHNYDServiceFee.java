
package com.asiainfo.veris.crm.order.soa.script.rule.fee;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistHNYDServiceFee extends BreBase implements IBREScript
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(ExistHNYDServiceFee.class);

    /**
     * 判断客服工号办理业务费用限制
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistHNYDServiceFee() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        
        //客服工号判断
        String staff_id = databus.getString("TRADE_STAFF_ID","000000");
        if(staff_id!=null&&!staff_id.startsWith("HNYD")){
        	return false;
        }

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeFeeSub = databus.getDataset("TF_B_TRADEFEE_SUB");
        
        if(IDataUtil.isNotEmpty(listTradeFeeSub)){

        	/* 开始逻辑规则校验 */
        	for (Iterator iter = listTradeFeeSub.iterator(); iter.hasNext();)
        	{
        		IData tradefeesub = (IData) iter.next();

        		if (tradefeesub.getInt("FEE") > 0)
        		{
        			if (logger.isDebugEnabled())
        				logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistHNYDServiceFee() " + true + "<<<<<<<<<<<<<<<<<<<");
        			return true;
        		}
        	}
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistHNYDServiceFee() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
