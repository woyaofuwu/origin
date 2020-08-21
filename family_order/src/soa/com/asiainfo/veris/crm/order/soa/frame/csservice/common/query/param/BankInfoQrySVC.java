
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BankInfoQrySVC extends CSBizService
{
    public IDataset getBankByBank(IData input) throws Exception
    {
        String eparchyCode = input.getString("EPARCHY_CODE");
        String superBankCode = input.getString("SUPER_BANK_CODE");
        String bankCode = input.getString("BANK_CODE");

        IDataset data = BankInfoQry.getBankByBank(eparchyCode, superBankCode, bankCode, getPagination());
        return data;
    }

    // 根据上级银行取下级银行,是否需要根据地区来取
    public IDataset getBankBySuperBank(IData input) throws Exception
    {
        String superBankCode = input.getString("SUPER_BANK_CODE");
        IDataset data = BankInfoQry.getBankBySuperBank(superBankCode, null);
        return data;
    }

    /**
     * 根据SUPER_BANK_CODE、EPARCHY_CODE、CITY_CODE，开户界面刷新银行列表逻辑
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getBankBySuperBank3(IData input) throws Exception
    {

        String superBankCode = input.getString("SUPER_BANK_CODE");
        String cityCode = input.getString("CITY_CODE");
        String eparchyCode = input.getString("EPARCHY_CODE");
        if (StringUtils.isBlank(cityCode))
        {
            cityCode = getVisit().getCityCode();
        }
        if (StringUtils.isBlank(eparchyCode))
        {
            eparchyCode = CSBizBean.getTradeEparchyCode();
        }
        return BankInfoQry.getBankBySuperBank3(superBankCode, cityCode, eparchyCode);
    }

    /**
     * 获取银行列表信息
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset qrySuperBankInfo(IData inParam) throws Exception
    {
        return BankInfoQry.qrySuperBankInfo();
    }

    public IDataset queryBackCode(IData input) throws Exception
    {
        String superBankCode = input.getString("SUPER_BANK_CODE");
        String eparchyCode = input.getString("EPARCHY_CODE");

        IDataset data = BankInfoQry.queryBackCode(eparchyCode, superBankCode, getPagination());
        return data;
    }
    
    public IDataset queryBackCodeCCT(IData input) throws Exception
    {
        String superBankCode = input.getString("SUPER_BANK_CODE");

        IDataset data = BankInfoQry.getBankBySuperBankCtt(superBankCode, getPagination());
        return data;
    }

    /**
     * 获取银行列表 费用组件外框使用
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryBankList(IData input) throws Exception
    {
        input.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        String superBankCode = input.getString("SUPER_BANK_CODE");
        if (superBankCode != null && !"".equals(superBankCode))
        {
            return BankInfoQry.qryBankInfoBySuperBankCode(superBankCode);
        }
        else
        {
            return BankInfoQry.qryBankInfoBySuperBankCode();
        }
    }

    /**
     * 账户编辑组件里面查询下级银行
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryBankListByBank(IData input) throws Exception
    {
        String superBankCode = input.getString("SUPER_BANK_CODE");
        String bankCode = input.getString("BANK_CODE", "").toUpperCase();
        String bank = input.getString("BANK", "").toUpperCase();
        return BankInfoQry.getBankByBank(CSBizBean.getTradeEparchyCode(), superBankCode, bankCode, bank);
    }
}
