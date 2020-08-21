
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.cmmb;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

public class CmmbAccountBalanceRule extends BreBase implements IBREScript
{

    protected static final Logger log = Logger.getLogger(CmmbAccountBalanceRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);
        String inModeCode = CSBizBean.getVisit().getInModeCode();
        PlatOfficeData officeData = PlatOfficeData.getInstance(psd.getElementId());

        if (PlatConstants.OPER_ORDER.equals(psd.getOperCode()))
        {
            String payMode = uca.getAccount().getPayModeCode();
            // 托收不需要判断余额
            if (!"1".equals(payMode))
            {
            	
            	IData oweFee = AcctCall.getOweFeeByUserId(uca.getUserId());
                String lastOweFee = oweFee.getString("LAST_OWE_FEE");
                String realFee = oweFee.getString("REAL_FEE");
                String leaveRealFee = oweFee.getString("ACCT_BALANCE");
                uca.setLastOweFee(lastOweFee);
                uca.setAcctBlance(leaveRealFee);
                uca.setRealFee(realFee);
            	

                float balance = Float.valueOf(uca.getAcctBlance());
                float price = Float.valueOf(officeData.getPrice()) * 100; // 元单位，化为分单位

                if (balance < price)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0985.toString(), PlatException.CRM_PLAT_0985.getValue());
                    return false;

                }
            }
        }

        return true;
    }

}
