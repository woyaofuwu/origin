
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.ailk.org.apache.commons.lang3.StringUtils;

public class CustContractProductInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 通过contractId查询合同产品信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryContractProductByContIdForGrp(IData input) throws Exception
    {
        String contractId = input.getString("CONTRACT_ID");
        String custId = input.getString("CUST_ID","");
        IDataset dataset = new DatasetList();
        if(StringUtils.isNotBlank(custId)){
            dataset = CustContractProductInfoQry.qryContractProductByContIdCustId(contractId,custId);
        }else {
            dataset = CustContractProductInfoQry.qryContractProductByContId(contractId);

        }

        return dataset;
    }

}
