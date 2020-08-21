
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.modifypayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PayTemplateBeanSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 查询是否支持高级付费标记
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryUserother(IData input) throws Exception
    {
        IDataset data = PayTemplateBean.qryUserother(input);
        return data;
    }

    public IDataset qryUserothers(IData input) throws Exception
    {
        IDataset data = PayTemplateBean.qryUserothers(input);
        return data;
    }

    /**
     * 查询费用限制
     */
    public IDataset showFeeLimit(IData input) throws Exception
    {
        IDataset data = PayTemplateBean.showFeeLimit(input);
        return data;
    }

    /**
     * 查询模板账目
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset showItems(IData input) throws Exception
    {
        IDataset data = PayTemplateBean.showItems(input);
        return data;
    }

    /**
     * 查询模板，支持区分地州
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset showTemplate(IData input) throws Exception
    {
        IDataset data = PayTemplateBean.showTemplate(input);
        return data;
    }
}
