
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

public class CheckOweFeeCreditForUPGP extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    /**
     * 201611011511规则
     * 集团统一付费产品欠费判断
     */
    private static Logger logger = Logger.getLogger(CheckOweFeeCreditForUPGP.class);
    
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckOweFeeCreditForUPGP()  >>>>>>>>>>>>>>>>>>");
        }
        
        String userId = databus.getString("USER_ID", "");//集团产品的user_id
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        // 设置默认值
        String acctBalance = "0"; // 实时结余
        
        try
        {
            IData oweFeeData = AcctCall.getOweFeeByUserId(userId);
            acctBalance = oweFeeData.getString("ACCT_BALANCE", "0");
            
            if (Double.parseDouble(acctBalance) < 0)
            {
                BreTipsHelp.addNorTipsInfo(databus, 
                        BreFactory.TIPS_TYPE_ERROR, 
                        errCode, "该集团客户目前已欠费，不能注销集团产品!");
                return false;
            }
        }
        catch(Exception e)
        {
            if(logger.isDebugEnabled())
            {
                logger.info(e);
            }

            if(logger.isInfoEnabled())
            {
                logger.info(e);
            }
            
            String err = "";
            err = e.getMessage();
            if(err.contains("CRM_BIZ_167"))
            {
                err = err.replace("[", "");
                err = err.replace("]", "");
                err = err.replace("`", "");
            }
            
            BreTipsHelp.addNorTipsInfo(databus, 
                    BreFactory.TIPS_TYPE_ERROR, errCode + "-999", err);
            return false;
        }

        if (logger.isDebugEnabled()) 
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckOweFeeCreditForUPGP() <<<<<<<<<<<<<<<<<<<");
        }
        
        return true;
    }

}
