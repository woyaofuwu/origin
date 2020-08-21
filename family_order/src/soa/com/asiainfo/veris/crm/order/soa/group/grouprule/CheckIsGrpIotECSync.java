
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpExtInfoQry;

public class CheckIsGrpIotECSync extends BreBase implements IBREScript
{

    /*
     * 判断集团客户资料是否同步.
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String custId = databus.getString("CUST_ID", "");// 当前客户标识
        String strBrandCode = databus.getString("BRAND_CODE", "");// 当前品牌标识
        String errCode = databus.getString("RULE_BIZ_ID");

        if ("WLWG".equals(strBrandCode))
        {
            IDataset ids = GrpExtInfoQry.getIotECSyncState(custId);
            if (ids.size() > 0)
            {
                String strRecCount = ids.getData(0).getString("RECORDCOUNT", "");
                if ("0".equals(strRecCount))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "物联网业务受限提示：该集团客户没有同步物联网EC信息，无法办理业务订购!");
                }
            }
        }

        return false;
    }

}
