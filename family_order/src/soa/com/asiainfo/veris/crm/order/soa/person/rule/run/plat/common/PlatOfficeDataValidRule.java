
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.common;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * 检验局数据是否有效 局数据在过期时，仍可以做退订 在做 订购 等其他操作时，局数据必须有效
 * 
 * @author xiekl
 */
public class PlatOfficeDataValidRule extends BreBase implements IBREScript
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -3070071463381113107L;

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);
        PlatOfficeData officeData = psd.getOfficeData();
        // 如果是非退订操作，且局数据不再有效
        if (!PlatConstants.OPER_CANCEL_ORDER.equals(psd.getOperCode()))
        {
            if (!PlatConstants.STATE_OK.equals(officeData.getBizStateCode()) || (PlatConstants.STATE_OK.equals(officeData.getBizStateCode()) && SysDateMgr.getSysDate().compareTo(officeData.getEndDate()) > 0))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, BofException.CRM_BOF_016.toString(), BofException.CRM_BOF_016.getValue());
                return false;
            }
        }
        return true;
    }

}
