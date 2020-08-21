package com.asiainfo.veris.crm.order.soa.group.modifydataline;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ModifyDataLineSVC extends GroupOrderService{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public final IDataset crtTrade(IData map) throws Exception {
        ModifyDataLineBean modifyDataLineBean = new ModifyDataLineBean();
        
        return modifyDataLineBean.crtTrade(map);
    }
}
