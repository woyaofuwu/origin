
package com.asiainfo.veris.crm.order.soa.script.rule.utils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class RuleUtils
{

    public static boolean existsUserById(IData databus) throws Exception
    {
        boolean bResut = true;

        IDataset result = databus.getDataset("TF_F_USER");
        if (IDataUtil.isEmpty(result))
        {
            bResut = false;
        }

        return bResut;

    }
}
