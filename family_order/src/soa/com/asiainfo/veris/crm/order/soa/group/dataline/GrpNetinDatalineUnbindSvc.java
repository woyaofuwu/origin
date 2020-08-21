
package com.asiainfo.veris.crm.order.soa.group.dataline;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;



public class GrpNetinDatalineUnbindSvc extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 互联网专线接入（专网专线）解绑
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset netinDatalineCrtTrade(IData inParam) throws Exception
    {
        GrpNetinDatalineUnbindBean bean = new GrpNetinDatalineUnbindBean();

        return bean.crtTrade(inParam);
    }
    
}                      