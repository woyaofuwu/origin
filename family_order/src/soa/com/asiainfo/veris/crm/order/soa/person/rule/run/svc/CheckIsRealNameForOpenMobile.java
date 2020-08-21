package com.asiainfo.veris.crm.order.soa.person.rule.run.svc;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

import org.apache.log4j.Logger;

/**
 * REQ201606270002 
 * chenxy3 20160704 
 * */
public class CheckIsRealNameForOpenMobile extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;
 
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String serialNumber = databus.getString("SERIAL_NUMBER", "");
        String removeTag = databus.getString("REMOVE_TAG", "0"); 
        if (!"".equals(serialNumber) && "0".equals(removeTag))
        {
            String isRealName = databus.getString("IS_REAL_NAME", "0");
            String acctTag = databus.getString("ACCT_TAG"); 
             
            if (!"1".equals(isRealName) && !"0".equals(acctTag)) 
            {   
               return true;
            }
        } 
        return false;
    }
}
