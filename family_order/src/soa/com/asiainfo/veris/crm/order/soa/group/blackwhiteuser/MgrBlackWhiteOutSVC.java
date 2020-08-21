
package com.asiainfo.veris.crm.order.soa.group.blackwhiteuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class MgrBlackWhiteOutSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData param) throws Exception
    {
        MgrBlackWhiteOutBean bean = new MgrBlackWhiteOutBean();

        return bean.crtTrade(param);
    }

}
