package com.asiainfo.veris.crm.order.soa.group.imscreditmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ChangeImsMebDesktopTelElementSvc extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    
    /**
     * 创建集团多媒体桌面电话成员的信控停开机订单
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtTrade(IData inParam) throws Exception
    {
    	ChangeImsMebDesktopTelElementBean bean = new ChangeImsMebDesktopTelElementBean();

        return bean.crtTrade(inParam);
    }
    
}
