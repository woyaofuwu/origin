
package com.asiainfo.veris.crm.order.soa.group.creategroupacct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.BankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class CreateGroupAcctSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inparam) throws Exception
    {
        CreateGroupAcctBean bean = new CreateGroupAcctBean();
        return bean.crtTrade(inparam);
    }
    
    /**
     * 根据上级银行获取银行数据
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset queryBankInfoBySup(IData params) throws Exception
    {
        String super_bank_code = params.getString("SUPERBANK_CODE");
        return BankInfoQry.getBankBySuperBank(super_bank_code, null);
    }

    /**
     * 根据银行名称或编码模糊查询
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset queryBurBankInfo(IData params) throws Exception
    {
        String super_bank_code = params.getString("SUPERBANK_CODE");
        String eparchy_code = CSBizBean.getTradeEparchyCode();
        String bank_code = params.getString("BANK_OR_CODE", "").toUpperCase();
        String bank = params.getString("BANK_OR_CODE", "").toUpperCase();
        return BankInfoQry.getBankByBank(eparchy_code, super_bank_code, bank_code, bank);
    }
    
}
