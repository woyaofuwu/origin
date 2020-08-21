
package com.asiainfo.veris.crm.order.soa.person.rule.run.interenetofthings;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.IotConstants;

/**
 * 物联网只能办理允许的业务类型
 * 
 * @author xiekl
 */
public class IotAllowedBusinessRule extends BreBase implements IBREScript
{

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String brandCode = databus.getString("BRAND_CODE");
        String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");

        // 非物联网号码不能做物联网的业务
        if (!"PWLW".equals(brandCode) && (tradeTypeCode.equals("272") || tradeTypeCode.equals("274") || tradeTypeCode.equals("279")|| tradeTypeCode.equals("280")))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 100, "只有物联网号码支持该业务类型[" + tradeTypeCode + "]");
            return false;
        }
        // 物联网暂时只能做 开户，产品变更，停开机，补换卡，立即销户，服务暂停恢复,客户资料变更
        else if ("PWLW".equals(brandCode) && IotConstants.ALLOW_TRADE_TYPE_CODE.indexOf(tradeTypeCode) == -1)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 100, "物联网号码不支持该业务类型[" + tradeTypeCode + "]");
            return false;
        }
        else
        {
            return true;
        }

    }

}
