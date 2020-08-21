
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.e3g;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class E3gAttrSameRule extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);
        if (PlatConstants.OPER_USER_DATA_MODIFY.equals(psd.getOperCode()))
        {
            List<AttrData> attrs = psd.getAttrs();
            for (AttrData attr : attrs)
            {
                if ("301".equals(attr.getAttrCode()))
                {
                    String attrValue = attr.getAttrValue();
                    List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcByServiceId(psd.getElementId());
                    if (userPlatSvcs != null && userPlatSvcs.size() > 0)
                    {
                        PlatSvcTradeData userPlatSvc = userPlatSvcs.get(0);
                        AttrTradeData attrTradeData = uca.getUserAttrsByRelaInstIdAttrCode(userPlatSvc.getInstId(), attr.getAttrCode());
                        if (attrValue.equals(attrTradeData.getAttrValue()))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0981.toString(), PlatException.CRM_PLAT_0981.getValue());
                        }
                    }
                }
            }
        }
        return true;
    }

}
