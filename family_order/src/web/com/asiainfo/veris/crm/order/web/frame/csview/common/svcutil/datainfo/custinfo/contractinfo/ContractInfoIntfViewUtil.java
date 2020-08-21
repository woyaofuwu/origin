
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.custinfo.contractinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.custinfo.contractinfo.ContractInfoIntf;

public class ContractInfoIntfViewUtil
{
    /**
     * 通过合同ID查询集团合同信息
     *
     * @param bc
     * @param contractId
     * @return
     * @throws Exception
     */
    public static IData qryContractByContractIdForGrp(IBizCommon bc, String contractId) throws Exception
    {
        return ContractInfoIntf.qryContractByContractIdForGrp(bc, contractId);
    }

    /**
     * 根据集团id查找合同信息
     *
     * @param bc
     * @param grpAcctId
     * @return
     * @throws Exception
     */
    public static IDataset qryContractByCustIdForGrp(IBizCommon bc, String CustId) throws Exception
    {
        return ContractInfoIntf.qryContractByCustIdForGrp(bc, CustId);
    }


    /**
     * 根据集团id查找合同信息
     *
     * @param bc
     * @param grpAcctId
     * @return
     * @throws Exception
     */
    public static IDataset qryContractByCustIdProductIdForGrp(IBizCommon bc, String custId,String productId,String lines) throws Exception
    {
        return ContractInfoIntf.qryContractByCustIdProductIdForGrp(bc, custId,productId,lines);
    }
}
