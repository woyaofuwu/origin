
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.bankinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class BankInfoIntf
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
        IData inparam = new DataMap();
        inparam.put("SUPER_BANK_CODE", superBankCode);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return CSViewCall.call(bc, "CS.BankInfoQrySVC.queryBackCode", inparam);
    }
    
    public static IDataset qryBankCCTInfosBySuperBankCodeAndEparchyCode(IBizCommon bc, String superBankCode, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SUPER_BANK_CODE", superBankCode);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return CSViewCall.call(bc, "CS.BankInfoQrySVC.queryBackCodeCCT", inparam);
    }

}
