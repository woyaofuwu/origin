
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;

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
public class CheckNpOutByVip extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam paramBreRuleParam) throws Exception
    {
        String custId = databus.getString("CUST_ID");
        IDataset ids = CustVipInfoQry.getCustVipsByCustIdRemoveTag(custId, "0");
        if (IDataUtil.isNotEmpty(ids))
        {
            String vipClassId = ids.getData(0).getString("VIP_CLASS_ID");
            String vipTypeCode = ids.getData(0).getString("VIP_TYPE_CODE");
            if ("1".equals(vipTypeCode) || "2".equals(vipTypeCode) || "3".equals(vipClassId) || "4".equals(vipClassId))
            {
                return true;
            }
        }
        return false;
    }

}
