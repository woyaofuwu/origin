
package com.asiainfo.veris.crm.order.soa.person.busi.badness.blackwhite;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BlackWhiteManageSVC extends CSBizService
{

    public IDataset addBlack(IData data) throws Exception
    {
        BlackWhiteManageBean bean = new BlackWhiteManageBean();
        return bean.addBlack(data);
    }

    public IDataset delBlack(IData data) throws Exception
    {
        BlackWhiteManageBean bean = new BlackWhiteManageBean();
        return bean.delBlack(data);
    }

    public IDataset updBlack(IData data) throws Exception
    {
        BlackWhiteManageBean bean = new BlackWhiteManageBean();
        return bean.updBlack(data);
    }
}
