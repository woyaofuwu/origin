
package com.asiainfo.veris.crm.order.soa.person.rule.run.simcardmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 判断用户是否订购了NFC手机钱包，如果订购了，就进行提示
 */
public class CheckUserMobileWallet extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String userId = databus.getString("USER_ID");
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        // 查询是否订购了UCF手机钱包业务
        if ("0".equals(xChoiceTag))
        {
            IDataset set = PlatSvcInfoQry.qryUserMobileWallet(userId);
            if (set != null && set.size() > 0)
            { // 如果订购了，就进行提示

                String msg = "换卡用户为中国移动NFC手机钱包用户，用户原NFC-SIM卡中可能存在余额，" + "请用户酌情保留原NFC-SIM卡，使用卡内余额可用于“闪付”消费或联系所绑定银行卡发卡行进行圈提。";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, 201509, msg);
            }

        }
        return false;
    }

}
