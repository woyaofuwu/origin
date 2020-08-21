
package com.asiainfo.veris.crm.order.soa.group.groupspecialopen;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GroupSpecialOpenSVC extends CSBizService
{
    public IDataset crtTrade(IData input) throws Exception
    {
        GroupSpecialOpenBean bean = new GroupSpecialOpenBean();
        return bean.crtTrade(input);
    }
}
