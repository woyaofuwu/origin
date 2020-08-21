
package com.asiainfo.veris.crm.order.soa.group.esop.lineworkformdata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class LineWorkformdataSVC extends CSBizService
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset qryLineWorkformdata(IData param) throws Exception
    {

        IDataset lineWorkformList = LineWorkformdataBean.qryLineWorkformByCondition(param, getPagination());

        return lineWorkformList;
    }
}
