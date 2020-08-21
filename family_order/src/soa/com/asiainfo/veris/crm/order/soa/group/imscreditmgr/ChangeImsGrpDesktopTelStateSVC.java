package com.asiainfo.veris.crm.order.soa.group.imscreditmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ChangeImsGrpDesktopTelStateSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 创建集团多媒体桌面电话的信控停开机订单
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtTrade(IData inParam) throws Exception
    {
    	ChangeImsGrpDesktopTelSvcElement bean = new ChangeImsGrpDesktopTelSvcElement();

        return bean.crtTrade(inParam);
    }
        
}
