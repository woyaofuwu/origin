
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GrpGenSnSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset genGrpSn(IData input) throws Exception
    {
        IData data = GrpGenSn.genGrpSn(input);
        IDataset result = new DatasetList();
        result.add(data);
        return result;
    }

}
