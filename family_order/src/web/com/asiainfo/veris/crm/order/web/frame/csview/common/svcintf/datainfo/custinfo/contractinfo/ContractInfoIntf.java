
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.custinfo.contractinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class ContractInfoIntf
{

    /**
     * 通过合同号查询集团合同信息
     *
     * @param bc
     * @param contractId
     * @return
     * @throws Exception
     */
    public static IData qryContractByContractIdForGrp(IBizCommon bc, String contractId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("CONTRACT_ID", contractId);
        return CSViewCall.callone(bc, "CS.CustContractInfoQrySVC.qryContractInfoByContractIdForGrp", inparam);
    }

    /**
     * 提供custid查询集团合同信息
     *
     * @param bc
     * @param CustId
     * @return
     * @throws Exception
     */
    public static IDataset qryContractByCustIdForGrp(IBizCommon bc, String CustId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("CUST_ID", CustId);
        return CSViewCall.call(bc, "CS.CustContractInfoQrySVC.qryContractByCustIdForGrp", inparam);
    }


    /**
     * 提供cust_id和product_id查询集团合同信息
     *
     * @param bc
     * @param CustId
     * @return
     * @throws Exception
     */
    public static IDataset qryContractByCustIdProductIdForGrp(IBizCommon bc, String custId,String productId,String lines) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("CUST_ID", custId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("LINE_NOS", lines);
        return CSViewCall.call(bc, "CM.ConstractGroupSVC.qryContractByCustIdProductId", inparam);

    }

}
