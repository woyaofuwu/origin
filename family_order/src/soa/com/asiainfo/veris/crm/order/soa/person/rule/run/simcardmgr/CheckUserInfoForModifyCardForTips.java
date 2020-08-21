
package com.asiainfo.veris.crm.order.soa.person.rule.run.simcardmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 判断用户是否VPMN用户，改号后将会把原有信息移植至新号码下 判断用户是否开通了来电拒接业务 判断用户是否订购了除biz_type_code=19以外的平台业务
 */
public class CheckUserInfoForModifyCardForTips extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String userId = databus.getString("USER_ID");
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        StringBuilder tipsInfo = new StringBuilder("改号业务提醒：");
        boolean tipsFlag = false;
        // 查询是否订购了UCF手机钱包业务
        if ("0".equals(xChoiceTag))
        {
            // VPMN判断
            IDataset uuset = RelaUUInfoQry.getRelationUUInfoByDeputySn(userId, "20", null);
            if (IDataUtil.isNotEmpty(uuset))
            {
                tipsInfo.append("<br>该用户是VPMN用户，改号后将会把原有信息移植至新号码下！");
                tipsFlag = true;
            }
            IDataset otherset = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "1301");
            if (IDataUtil.isNotEmpty(otherset))
            {
                tipsInfo.append("<br>该用户开通了来电拒接业务，改号后将会把所有拒接号码移植至新号码下！");
                tipsFlag = true;
            }
            IDataset svcset = UserPlatSvcInfoQry.queryUserPlatOtherInfoByUserId(userId, "19");
            if (IDataUtil.isNotEmpty(svcset))
            {
                tipsInfo.append("<br>该用户开通了平台业务，改号后将会退订除无线音乐会员以外的其他平台业务！");
                tipsFlag = true;
            }
            if (tipsFlag)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 201510, tipsInfo.toString());
            }
        }
        return false;
    }

}
