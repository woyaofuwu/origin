
package com.asiainfo.veris.crm.order.soa.person.rule.run.staff;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 测试号业务办理限制
 * 
 * @author liutt
 */
public class CheckTestSerialNumberLimit extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String inModeCode = getVisit().getInModeCode();
        if ("0".equals(inModeCode) || "1".equals(inModeCode) || "3".equals(inModeCode))
        {
            IDataset ds = CommparaInfoQry.getCommparaCode1("CSM", "558", databus.getString("CITY_CODE"), databus.getString("EPARCHY_CODE"));
            if (IDataUtil.isNotEmpty(ds))
            {
                IDataset ds2 = CommparaInfoQry.getCommparaCode1("CSM", "559", getVisit().getStaffId(), databus.getString("EPARCHY_CODE"));
                if (IDataUtil.isEmpty(ds2))
                {
                    return true;// 该工号不能办理测试号码的该业务！
                }

            }
        }
        return false;
    }

}
