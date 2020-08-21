
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.hscs;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class HscsOperRule extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // TODO Auto-generated method stub
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);
        if (!PlatConstants.OPER_CANCEL_ORDER.equals(psd.getOperCode()) && "6".equals(CSBizBean.getVisit().getInModeCode()))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0999_6.toString(), PlatException.CRM_PLAT_0999_6.getValue());
            return false;
        }
        return true;
    }

}
