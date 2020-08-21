
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ContractResourceInfoQry
{

    public static IDataset queryContractResourceByRCode(String resourceCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("RESOURCE_CODE", resourceCode);
        return Dao.qryByCode("TI_B_CONTRACT_RESOURCE", "SEL_BY_RESOURCECODE", inparam);
    }
}
