
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.farm;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class Max3OrderRule extends BreBase implements IBREScript
{

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcTradeData pstd = (PlatSvcTradeData) databus.get(PlatConstants.RULE_PLATSVC_TRADE);
        if (PlatConstants.OPER_ORDER.equals(pstd.getOperCode()))
        {
            PlatSvcData psd = (PlatSvcData) pstd.getPmd();
            String bizCode = psd.getOfficeData().getBizCode();
            int index = bizCode.indexOf("-");
            if (index > 0 && bizCode.indexOf("-", index + 1) > 0)
            {
                // 表示是农信通百事易
                List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcs();
                int size = userPlatSvcs.size();
                int count = 0;
                for (int i = 0; i < size; i++)
                {
                    PlatSvcTradeData userPlatSvc = userPlatSvcs.get(i);
                    if (!BofConst.MODIFY_TAG_DEL.equals(userPlatSvc.getModifyTag()))
                    {
                        if (PlatConstants.STATE_CANCEL.equals(userPlatSvc.getBizStateCode()))
                        {
                            continue;
                        }
                        PlatOfficeData officeData = PlatOfficeData.getInstance(userPlatSvc.getElementId());
                        bizCode = officeData.getBizCode();
                        index = bizCode.indexOf("-");
                        if (index > 0 && bizCode.indexOf("-", index + 1) > 0)
                        {
                            count++;
                        }
                    }
                }
                if (count > 3)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0999_4.toString(), PlatException.CRM_PLAT_0999_4.getValue());
                    return false;
                }
            }
        }
        return true;
    }

}
