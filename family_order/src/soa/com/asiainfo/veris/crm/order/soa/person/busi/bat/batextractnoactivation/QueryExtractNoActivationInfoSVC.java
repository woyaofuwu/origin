
package com.asiainfo.veris.crm.order.soa.person.busi.bat.batextractnoactivation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ExtractNoActivationQry;

public class QueryExtractNoActivationInfoSVC extends CSBizService
{
    /**
     * 执行号码激活情况信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryExtractnoactivationInfo(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        result = ExtractNoActivationQry.queryExtractnoactivationInfo(input, this.getPagination());
        return result;
    }
}
