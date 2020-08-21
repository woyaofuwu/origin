
package com.asiainfo.veris.crm.order.soa.group.changevpmnscpcode;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ChangeVpmnScpCodeSVC extends CSBizService
{
    public final IDataset crtOrder(IData map) throws Exception
    {
        // 调用bean封装成和商品数据结构一致的数据包

        ChangeVpmnScpCodeBean bean = new ChangeVpmnScpCodeBean();
        return bean.crtOrder(map);
    }

}
