
package com.asiainfo.veris.crm.order.soa.group.colorringOpenSpecial;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ColorringOpenSpecSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inparam) throws Exception
    {
        ColorringOpenSpecBean bean = new ColorringOpenSpecBean();
        return bean.crtTrade(inparam);
    }

}
