package com.asiainfo.veris.crm.order.soa.person.busi.data;

import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public class DataDonateSVC extends CSBizService {

    public IDataset getCommInfo(IData input) throws Exception
    {
    	DataDonateBean bean = new DataDonateBean();
        return bean.getCommInfo(input);
    }
    
    public IData queryObjCustInfo(IData input) throws Exception
    {
    	DataDonateBean bean = new DataDonateBean();
        return bean.queryObjCustInfo(input);
    }
}
