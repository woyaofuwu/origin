
package com.asiainfo.veris.crm.order.soa.group.dataline;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;



public class GrpUserDatalineUnbindSvc extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 数据专线（专网专线）解绑
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset grpUserDatalineCrtTrade(IData inParam) throws Exception
    {
        GrpUserDatalineUnbindBean bean = new GrpUserDatalineUnbindBean();

        return bean.crtTrade(inParam);
    }
    
}                      