
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GroupImsUtilSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 成员新增分机号校验[融合总机]
     * 
     * @param indata
     * @return
     * @throws Exception
     */
    public IDataset shortCodeValidateSupTeleMeb(IData indata) throws Exception
    {
        boolean flag = GroupImsUtil.shortCodeValidateSupTeleMeb(indata);
        IData data = new DataMap();
        data.put("FLAG", flag);
        data.put("ERROR_MESSAGE", indata.getString("ERROR_MESSAGE"));
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }

}
