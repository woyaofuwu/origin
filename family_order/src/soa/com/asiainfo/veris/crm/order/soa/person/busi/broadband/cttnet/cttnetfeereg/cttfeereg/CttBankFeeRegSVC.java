
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetfeereg.cttfeereg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CttBankFeeRegSVC extends CSBizService
{
    public IDataset queryBankFeeRegs(IData input) throws Exception
    {
        CttBankFeeRegBean bean = (CttBankFeeRegBean) BeanManager.createBean(CttBankFeeRegBean.class);
        return bean.queryBankFeeRegs(input);
    }

    public IDataset queryBankFeeRegTotle(IData input) throws Exception
    {
        CttBankFeeRegBean bean = (CttBankFeeRegBean) BeanManager.createBean(CttBankFeeRegBean.class);
        return bean.queryBankFeeRegTotle(input);
    }

    public IDataset saveBankFeeReg(IData input) throws Exception
    {
        CttBankFeeRegBean bean = (CttBankFeeRegBean) BeanManager.createBean(CttBankFeeRegBean.class);
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        if ("1".equals(input.getString("MethodFlag")))
        {
            data.put("LOG_ID", bean.saveBankFeeRegM(input));
        }
        else
        {
            data.put("LOG_ID", bean.saveBankFeeReg(input));
        }
        dataset.add(data);
        return dataset;
    }
}
