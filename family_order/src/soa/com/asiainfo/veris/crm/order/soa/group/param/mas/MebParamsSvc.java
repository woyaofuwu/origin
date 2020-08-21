
package com.asiainfo.veris.crm.order.soa.group.param.mas;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class MebParamsSvc extends CSBizService
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
}
