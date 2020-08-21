
package com.asiainfo.veris.crm.order.soa.group.yunmas;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class YunmasynMebSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData param) throws Exception
    {
        YunmasynMebBean bean = new YunmasynMebBean();

        return bean.crtTrade(param);
    }
}
