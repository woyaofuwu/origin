
package com.asiainfo.veris.crm.order.soa.group.upgprelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class DesUnifyPayRelationSVC extends GroupOrderService
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 集团统一付费产品成员注销(统付注销)
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset crtTrade(IData data) throws Exception
    {
        DesUnifyPayRelationBean bean = new DesUnifyPayRelationBean();
        return bean.crtTrade(data);
    }
    
}
