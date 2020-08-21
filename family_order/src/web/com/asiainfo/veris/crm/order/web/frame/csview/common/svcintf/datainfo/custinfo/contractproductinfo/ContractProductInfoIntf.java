
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.custinfo.contractproductinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class ContractProductInfoIntf
{

    /**
     * 根据合同ID查询合同产品信息
     * 
     * @param bc
     * @param CustId
     * @return
     * @throws Exception
     */
    public static IDataset qryContractProductByContIdForGrp(IBizCommon bc, String contractId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("CONTRACT_ID", contractId);
        return CSViewCall.call(bc, "CS.CustContractProductInfoQrySVC.qryContractProductByContIdForGrp", inparam);
    }

}
