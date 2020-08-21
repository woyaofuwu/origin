
package com.asiainfo.veris.crm.order.soa.group.validchk;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GroupValidateSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset shortCodeValidateSupTeleMeb(IData inparam) throws Exception
    {
        boolean flag = GroupValidate.shortCodeValidateSupTeleMeb(inparam);
        IData data = new DataMap();
        data.put("FLAG", flag);
        data.put("ERROR_MESSAGE", inparam.getString("ERROR_MESSAGE"));
        IDataset ds = new DatasetList();
        ds.add(data);
        return ds;
    }

    /**
     * 总机号码验证[移动总机]
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset shortNumValidateSuperTeleAdmin(IData inparam) throws Exception
    {
        boolean flag = GroupValidate.shortNumValidateSuperTeleAdmin(inparam);
        IData data = new DataMap();
        data.put("FLAG", flag);
        data.put("ERROR_MESSAGE", inparam.getString("ERROR_MESSAGE"));
        IDataset ds = new DatasetList();
        ds.add(data);
        return ds;
    }
}
