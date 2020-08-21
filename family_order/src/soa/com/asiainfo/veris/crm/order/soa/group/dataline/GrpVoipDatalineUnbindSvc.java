
package com.asiainfo.veris.crm.order.soa.group.dataline;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;



public class GrpVoipDatalineUnbindSvc extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * VOIP专线（专网专线）解绑
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset voipDatalineCrtTrade(IData inParam) throws Exception
    {
        GrpVoipDatalineUnbindBean bean = new GrpVoipDatalineUnbindBean();

        return bean.crtTrade(inParam);
    }
    
}                      