
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.param;

import com.ailk.biz.util.StaticUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public final class UBankInfoQry
{
    /**
     * 根据银行编码查询银行名称
     * 
     * @param bankCode
     * @return
     * @throws Exception
     */
    public static String getBankNameByBankCode(String bankCode) throws Exception
    {
        String bankName = "";

        if ("0".equals(bankCode))
        {
            bankName = "其他";
        }
        else
        {
            bankName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BANK", new String[]
            { "BANK_CODE", "EPARCHY_CODE" }, "BANK", new String[]
            { bankCode, CSBizBean.getTradeEparchyCode() });
        }

        return bankName;
    }

    /**
     * 根据当前银行编码查询上级银行编码
     * 
     * @param bankCode
     * @return
     * @throws Exception
     */
    public static String getSuperBankCodeByBankCode(String bankCode) throws Exception
    {
        return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BANK", new String[]
        { "BANK_CODE", "EPARCHY_CODE" }, "SUPER_BANK_CODE", new String[]
        { bankCode, CSBizBean.getTradeEparchyCode() });
    }

    /**
     * 根据上级银行编码查询上级银行名称
     * 
     * @param superBankCode
     * @return
     * @throws Exception
     */
    public static String getSuperBankNameBySuperBankCode(String superBankCode) throws Exception
    {
        return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_SUPERBANK", "SUPER_BANK_CODE", "SUPER_BANK", superBankCode);
    }
}
