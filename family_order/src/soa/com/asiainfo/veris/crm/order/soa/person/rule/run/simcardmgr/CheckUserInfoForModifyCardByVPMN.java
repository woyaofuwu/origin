
package com.asiainfo.veris.crm.order.soa.person.rule.run.simcardmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 只有客服数据班工号才能对移动公司的VPMN用户办理改号业务!
 */
public class CheckUserInfoForModifyCardByVPMN extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String userId = databus.getString("USER_ID");
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if ("0".equals(xChoiceTag))
        {
            // VPMN判断
            IDataset uuset = RelaUUInfoQry.getRelationUUInfoByDeputySn(userId, "20", null);
            if (IDataUtil.isNotEmpty(uuset) && "V0HN001010".equals(uuset.getData(0).getString("SERIAL_NUMBER_A")))
            {
                IDataset comset = CommparaInfoQry.getCommpara("CSM", "870", CSBizBean.getVisit().getStaffId(), getTradeEparchyCode());
                if (IDataUtil.isEmpty(comset))
                {
                    // common.error("只有客服数据班工号才能对移动公司的VPMN用户办理改号业务!");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201509, "只有客服数据班工号才能对移动公司的VPMN用户办理改号业务!");
                }
            }
        }
        return false;
    }
}
