package com.asiainfo.veris.crm.order.soa.group.groupintf.credit.esop;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EsopCreditSVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset queryPauseBackLines(IData inparam) throws Exception
    {
        EsopCreditBean bean = new EsopCreditBean();
        return bean.queryPauseBackLines(inparam);
    }
    public IDataset queryTradeLines(IData inparam) throws Exception
    {
        EsopCreditBean bean = new EsopCreditBean();
        return bean.queryTradeLines(inparam);
    }
}
