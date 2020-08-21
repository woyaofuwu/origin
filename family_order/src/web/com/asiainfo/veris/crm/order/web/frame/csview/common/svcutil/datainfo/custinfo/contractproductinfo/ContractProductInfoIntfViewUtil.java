
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.custinfo.contractproductinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.custinfo.contractproductinfo.ContractProductInfoIntf;

public class ContractProductInfoIntfViewUtil
{
    /**
     * 根据合同ID查找集团的合同产品信息
     * 
     * @param bc
     * @param grpAcctId
     * @return
     * @throws Exception
     */
    public static IDataset qryContractProductByContIdForGrp(IBizCommon bc, String CustId) throws Exception
    {
        return ContractProductInfoIntf.qryContractProductByContIdForGrp(bc, CustId);
    }

}
