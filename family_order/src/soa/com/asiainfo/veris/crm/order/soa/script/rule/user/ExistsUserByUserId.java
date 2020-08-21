
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * 判断是否存在用户
 * 
 * @author Administrator
 */
public class ExistsUserByUserId extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
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
