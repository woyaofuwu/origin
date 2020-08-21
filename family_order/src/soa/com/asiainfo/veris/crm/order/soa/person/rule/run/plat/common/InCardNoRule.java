
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.common;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;

public class InCardNoRule extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // TODO Auto-generated method stub
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);

        String inCardNo = psd.getInCardNo();
        if (StringUtils.isNotBlank(inCardNo))
        {

            if (PlatConstants.OPER_ORDER.equals(psd.getOperCode()))
            {
                IDataset result = UserPlatSvcInfoQry.queryInCardNo(inCardNo, psd.getOfficeData().getOrgDomain());
                if (IDataUtil.isNotEmpty(result))
                {
                    if (PlatConstants.PLAT_CMRB_Bing.equals(psd.getOfficeData().getBizTypeCode()))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0968_1.toString(), PlatException.CRM_PLAT_0968_1.getValue());
                    }
                    else if (PlatConstants.PLAT_E3G.equals(psd.getOfficeData().getBizTypeCode()) || PlatConstants.PLAT_TYGM.equals(psd.getOfficeData().getBizTypeCode()))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0912_7.toString(), PlatException.CRM_PLAT_0912_7.getValue());
                    }
                    return false;
                }
            }
            else
            {
                List<PlatSvcTradeData> pstds = uca.getUserPlatSvcByServiceId(psd.getElementId());
                if (pstds != null && pstds.size() > 0)
                {
                    if (!inCardNo.equals(pstds.get(0).getInCardNo()))
                    {
                        if (PlatConstants.PLAT_CMRB_Bing.equals(psd.getOfficeData().getBizTypeCode()))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0968_2.toString(), PlatException.CRM_PLAT_0968_2.getValue());
                        }
                        else if (PlatConstants.PLAT_E3G.equals(psd.getOfficeData().getBizTypeCode()) || PlatConstants.PLAT_TYGM.equals(psd.getOfficeData().getBizTypeCode()))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0979_2.toString(), PlatException.CRM_PLAT_0979_2.getValue());
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
