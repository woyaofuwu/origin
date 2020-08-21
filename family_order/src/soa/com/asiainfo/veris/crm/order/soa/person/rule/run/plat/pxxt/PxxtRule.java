
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.pxxt;


import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class PxxtRule extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        IDataset limitServices = UserDiscntInfoQry.queryUserValidDiscntByPackageId(uca.getUserId(),"91500102");
        int  recordCount = limitServices.size();
        if(recordCount>0)
        {
        	  BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_3031.toString(), PlatException.CRM_PLAT_3031.getValue() );
              return false;
        }
        
        
        return true;
    }

}
