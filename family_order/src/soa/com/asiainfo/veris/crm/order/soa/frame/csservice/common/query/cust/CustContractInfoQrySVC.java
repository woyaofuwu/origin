
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CustContractInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 通过CUSTID查询集团合同信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryContractByCustIdForGrp(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");
        IDataset dataset = CustContractInfoQry.qryContractByCustId(custId);

        return dataset;
    }

    /**
     * 通过合同编码查询集团合同信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryContractInfoByContractIdForGrp(IData input) throws Exception
    {
        String contractId = input.getString("CONTRACT_ID");
        IDataset dataset = CustContractInfoQry.qryContractInfoByContractId(contractId);

        return dataset;
    }

}
