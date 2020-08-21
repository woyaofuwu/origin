
package com.asiainfo.veris.crm.order.soa.person.busi.badness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BadInfoComplainDealSVC extends CSBizService
{

    public IDataset badInfoActive(IData data) throws Exception
    {
        BadInfoComplainDealBean bean = new BadInfoComplainDealBean();
        return bean.badInfoActive(data);
    }

    public IDataset getServRequestType(IData data) throws Exception
    {
        BadInfoComplainDealBean bean = new BadInfoComplainDealBean();
        return bean.getServRequestType(data);

    }
    
    public IDataset getFourthTypeCodes(IData data) throws Exception
    {
        BadInfoComplainDealBean bean = new BadInfoComplainDealBean();
        return bean.getFourthTypeCodes(data);

    }
    
    public IDataset getFifthTypeCodes(IData data) throws Exception
    {
        BadInfoComplainDealBean bean = new BadInfoComplainDealBean();
        return bean.getFifthTypeCodes(data);

    }
    
    public IDataset getSevenTypeCodes(IData data) throws Exception
    {
        BadInfoComplainDealBean bean = new BadInfoComplainDealBean();
        return bean.getSevenTypeCodes(data);

    }
}
