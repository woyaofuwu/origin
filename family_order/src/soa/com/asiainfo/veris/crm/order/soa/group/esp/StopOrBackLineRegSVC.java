package com.asiainfo.veris.crm.order.soa.group.esp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class StopOrBackLineRegSVC extends GroupOrderService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset crtOrder(IData param) throws Exception {
        StopOrBackLineRegBean bean = new StopOrBackLineRegBean();
        return bean.crtOrder(param);
    }

}
