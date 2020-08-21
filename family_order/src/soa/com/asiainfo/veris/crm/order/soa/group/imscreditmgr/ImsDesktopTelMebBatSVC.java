package com.asiainfo.veris.crm.order.soa.group.imscreditmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ImsDesktopTelMebBatSVC extends GroupOrderService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 提供给信控调用，批量创建IMS集团多媒体桌面电话的成员停机
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtStopBat(IData inParam) throws Exception
    {
    	ImsDesktopTelMebBatBean bean = new ImsDesktopTelMebBatBean();

        return bean.crtStopBat(inParam);
    }
    
    /**
     * 提供给信控调用，批量创建IMS集团多媒体桌面电话的成员开机
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtOpenBat(IData inParam) throws Exception
    {
    	ImsDesktopTelMebBatBean bean = new ImsDesktopTelMebBatBean();

        return bean.crtOpenBat(inParam);
    }

}
