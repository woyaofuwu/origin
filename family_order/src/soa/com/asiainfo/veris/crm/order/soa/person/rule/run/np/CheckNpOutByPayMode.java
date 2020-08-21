
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckNpOutByOweFeeStop.java
 * @Description: 43--携出欠停（携入方落地）
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-6-26 下午7:54:33 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-6-26 lijm3 v1.0.0 修改原因
 */
public class CheckNpOutByPayMode extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam paramBreRuleParam) throws Exception
    {
        String sn = databus.getString("SERIAL_NUMBER");
        IDataset ids = AcctInfoQry.getAcctInfosBysn(sn);
        if (IDataUtil.isNotEmpty(ids))
        {
            for (int i = 0, len = ids.size(); i < len; i++)
            {
                String payModeCose = ids.getData(i).getString("PAY_MODE_CODE");
                if ("1".equals(payModeCose))
                {
                    return true;
                }
            }
        }
        return false;
    }

}
