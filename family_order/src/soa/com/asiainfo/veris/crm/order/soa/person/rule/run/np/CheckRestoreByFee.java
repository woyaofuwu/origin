
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;

public class CheckRestoreByFee extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData dataBus, BreRuleParam paramBreRuleParam) throws Exception
    {
        int fee = dataBus.getInt("FEE");
        IDataset ids = TagInfoQry.getTagInfo(CSBizBean.getTradeEparchyCode(), "CS_CHR_JUGDEOWEFEETAG", "0", null);
        if (IDataUtil.isNotEmpty(ids))
        {
            IData tagdata = (IData) ids.get(0);
            String staginfo = tagdata.getString("TAG_INFO", "");
            if (staginfo.startsWith("1"))
            {
                if (fee < 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

}
