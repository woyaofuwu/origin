
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.common;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * 营销活动未到期，不允许退订
 * 
 * @author xiekl
 */
public class SaleActiveUndueRule implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);

        if (PlatConstants.OPER_CANCEL_ORDER.equals(psd.getOperCode()))
        {
            IDataset saleActiveList = UserSaleActiveInfoQry.queryUserPlatSvcOrderBySaleActive(uca.getUserId(), psd.getElementId(), "K", CSBizBean.getTradeEparchyCode());
            if (saleActiveList != null && !saleActiveList.isEmpty())
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0905.toString(), PlatException.CRM_PLAT_0905.getValue());

                return false;
            }
        }

        return true;
    }

}
