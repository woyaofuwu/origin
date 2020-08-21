
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.common;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
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

public class PlatRepeatOperRule extends BreBase implements IBREScript
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 7020339251349974781L;

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);

        if ("64".equals(psd.getOprSource()))
        {
            return true;
        }

        List<PlatSvcTradeData> pstds = uca.getUserPlatSvcByServiceId(psd.getElementId());
        if (pstds != null && pstds.size() > 0)
        {
            PlatSvcTradeData userPlatSvc = pstds.get(0);
            
            if (BofConst.MODIFY_TAG_ADD.equals(psd.getModifyTag()))
            {
            	PlatOfficeData offDate = psd.getOfficeData();
            	//和生活业务时，如果用户已存在的和生活服务在下月初之前失效，则允许再次订购一个新和生活业务，视为业务续订操作.
            	if ("78".equals(offDate.getBizTypeCode()) && SysDateMgr.getFirstDayOfNextMonth().compareTo(userPlatSvc.getEndDate()) > 0){
            		return true;
            	}
            	
                // 如果用户当前订购的平台服务，是在用户也有的平台服务到期后订购（属于续订的情况）,则不报错
                if (StringUtils.isNotBlank(psd.getStartDate()) && StringUtils.isNotBlank(userPlatSvc.getEndDate()) && userPlatSvc.getEndDate().compareTo(psd.getStartDate()) < 0)
                {
                    return true;
                }

                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0912_6.toString(), PlatException.CRM_PLAT_0912_6.getValue());
                return false;
            }
            if (PlatConstants.STATE_PAUSE.equals(userPlatSvc.getBizStateCode()) && !(PlatConstants.OPER_RESTORE.equals(psd.getOperCode()) || PlatConstants.OPER_CANCEL_ORDER.equals(psd.getOperCode())))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0912_5.toString(), PlatException.CRM_PLAT_0912_5.getValue());
                return false;
            }
            if (PlatConstants.STATE_LOSE.equals(userPlatSvc.getBizStateCode()) && !PlatConstants.OPER_UNLOSE.equals(psd.getOperCode()))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0979_1.toString(), PlatException.CRM_PLAT_0979_1.getValue());
                return false;
            }
            else if (PlatConstants.STATE_OK.equals(userPlatSvc.getBizStateCode()) && PlatConstants.OPER_RESTORE.equals(psd.getOperCode()))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0912_6.toString(), PlatException.CRM_PLAT_0912_6.getValue());
                return false;
            }
        }
        else
        {
            if (!BofConst.MODIFY_TAG_ADD.equals(psd.getModifyTag()))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0913.toString(), PlatException.CRM_PLAT_0913.getValue());
                return false;
            }
        }

        return true;
    }

}
