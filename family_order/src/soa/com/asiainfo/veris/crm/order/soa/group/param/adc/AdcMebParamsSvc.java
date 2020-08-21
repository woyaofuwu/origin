
package com.asiainfo.veris.crm.order.soa.group.param.adc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class AdcMebParamsSvc extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public IDataset getServiceParam(IData input) throws Exception
    {
        MemParams memparam = new MemParams();
        IDataset output = memparam.getServiceParam(input);
        return output;
    }
    
    public IDataset getMebService(IData input) throws Exception
    {
        MemParams memparam = new MemParams();
        IDataset output = memparam.getmebServIdByGrpServId(input);
        return output;
    }
}
