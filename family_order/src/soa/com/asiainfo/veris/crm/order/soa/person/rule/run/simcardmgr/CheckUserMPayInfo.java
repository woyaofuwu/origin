
package com.asiainfo.veris.crm.order.soa.person.rule.run.simcardmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 判断用户是否有手机支付业务
 */
public class CheckUserMPayInfo extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        String userId = databus.getString("USER_ID");
        String msg = "该用户开通了手机支付功能，选择【确定】继续换卡，【取消】用户先自行去取消手机支付功能后再来办理换卡业务";
        if ("0".equals(xChoiceTag))
        {
            IDataset mpayInfos = PlatSvcInfoQry.qryUserMPayInfo(userId, "54");
            if (IDataUtil.isNotEmpty(mpayInfos))
            {
                IDataset mPayInfos = IBossCall.changCard2PayPlat(getVisit(), databus.getString("SERIAL_NUMBER"));
                if (IDataUtil.isNotEmpty(mPayInfos))
                {
                    if ("0000".equals(mPayInfos.getData(0).getString("DEAL_RESULT")))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, 201509, msg);
                    }
                }
            }
        }
        return false;
    }

}
