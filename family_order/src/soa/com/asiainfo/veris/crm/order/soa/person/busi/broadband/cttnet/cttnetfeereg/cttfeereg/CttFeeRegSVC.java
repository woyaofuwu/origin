
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetfeereg.cttfeereg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CttFeeRegSVC extends CSBizService
{
    public IDataset qryFeeRegCTT(IData input) throws Exception
    {
        CttFeeRegBean bean = (CttFeeRegBean) BeanManager.createBean(CttFeeRegBean.class);
        return bean.qryFeeRegCTT(input);
    }

    public IDataset queryFeeRegTotle(IData input) throws Exception
    {
        CttFeeRegBean bean = (CttFeeRegBean) BeanManager.createBean(CttFeeRegBean.class);
        return bean.queryFeeRegTotle(input);
    }

    public IDataset saveFeeReg(IData input) throws Exception
    {
        CttFeeRegBean bean = (CttFeeRegBean) BeanManager.createBean(CttFeeRegBean.class);
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        if ("1".equals(input.getString("MethodFlag")))
        {
            data.put("LOG_ID", bean.saveFeeRegM(input));
        }
        else
        {
            data.put("LOG_ID", bean.saveFeeReg(input));
        }
        dataset.add(data);
        return dataset;
    }
}
