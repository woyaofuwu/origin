
package com.asiainfo.veris.crm.order.soa.group.returnratio;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ReturnRationSVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inParam) throws Exception
    {
        ReturnRationBean bean = new ReturnRationBean();

        return bean.crtTrade(inParam);
    }

    public IDataset queryGroupProductInfo(IData inParam) throws Exception
    {
        ReturnRationBean bean = new ReturnRationBean();

        return bean.queryGroupProductInfo(inParam, getPagination());
    }

    public IDataset queryProduct(IData inParam) throws Exception
    {
        ReturnRationBean bean = new ReturnRationBean();
        return bean.queryProduct(inParam);
    }

    public IDataset queryRatioOtherInfo(IData inParam) throws Exception
    {
        ReturnRationBean bean = new ReturnRationBean();
        return bean.queryRatioOtherInfo(inParam, getPagination());
    }
    
}
