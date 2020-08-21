
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.bankinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.bankinfo.BankInfoIntf;

public class BankInfoIntfViewUtil
{
    /**
     * 通过上级银行编码SUPER_BANK_CODE和地州编码EPARCHY_CODE查询上级银行下包含的银行信息列表
     * 
     * @param bc
     * @param superBankCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryBankInfosBySuperBankCodeAndEparchyCode(IBizCommon bc, String superBankCode, String eparchyCode) throws Exception
    {
        IDataset attrItems = BankInfoIntf.qryBankInfosBySuperBankCodeAndEparchyCode(bc, superBankCode, eparchyCode);
        return attrItems;
    }
    
    public static IDataset qryBankCCTInfosBySuperBankCodeAndEparchyCode(IBizCommon bc, String superBankCode, String eparchyCode) throws Exception
    {
        IDataset attrItems = BankInfoIntf.qryBankCCTInfosBySuperBankCodeAndEparchyCode(bc, superBankCode, eparchyCode);
        return attrItems;
    }

}
