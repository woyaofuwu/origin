
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.farm;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * 农信通 包年套餐不能退订
 * 
 * @author xiekl
 */
public class FarmYearDiscntRule implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);
        if (PlatConstants.OPER_CANCEL_ORDER.equals(psd.getOperCode()) || PlatConstants.OPER_CANCEL_ALL.equals(psd.getOperCode()))
        {
            UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
            List<DiscntTradeData> userDiscntList = uca.getUserDiscntsByDiscntCodeArray("2160,2161");
            if (userDiscntList != null && !userDiscntList.isEmpty())
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0908_1.toString(), PlatException.CRM_PLAT_0908_1.getValue());
                return false;
            }
        }

        return true;
    }

}
