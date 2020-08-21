
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckReturnNumByNum.java
 * @Description:号码归还
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-6-26 下午7:32:32 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-6-26 lijm3 v1.0.0 修改原因
 */
public class CheckReturnNumByNum extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam paramBreRuleParam) throws Exception
    {
        String sn = databus.getString("SERIAL_NUMBER");
        IDataset ids = MsisdnInfoQry.getMsisdnsBySerialNumber_c(sn);
        if (IDataUtil.isEmpty(ids))
        {
            return true;
        }
        return false;
    }

}
