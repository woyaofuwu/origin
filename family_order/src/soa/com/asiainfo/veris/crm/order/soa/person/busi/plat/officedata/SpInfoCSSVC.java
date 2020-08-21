
package com.asiainfo.veris.crm.order.soa.person.busi.plat.officedata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SpInfoCSSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset deleteSpInfoCS(IData params) throws Exception
    {
        SpInfoCSBean bean = new SpInfoCSBean();
        IDataset res = bean.deleteSpInfoCS(params);
        return res;
    }

    public IDataset insertSpInfoCS(IData params) throws Exception
    {
        SpInfoCSBean bean = new SpInfoCSBean();
        IDataset res = bean.insertSpInfoCS(params);
        return res;
    }

    public IDataset querySpInfoCS(IData param) throws Exception
    {
        SpInfoCSBean bean = new SpInfoCSBean();
        IDataset res = bean.querySpInfoCS(param, getPagination());
        return res;
    }

    public IDataset querySpInfoCSByPkRecordStatus(IData param) throws Exception
    {
        SpInfoCSBean bean = new SpInfoCSBean();
        IDataset res = bean.querySpInfoCSByPkRecordStatus(param);
        return res;
    }

    public IDataset querySpInfoCSByRowID(IData param) throws Exception
    {
        SpInfoCSBean bean = new SpInfoCSBean();
        IDataset res = bean.querySpInfoCSByRowID(param);
        return res;
    }

    public IDataset updateSpInfoCS(IData params) throws Exception
    {
        SpInfoCSBean bean = new SpInfoCSBean();
        IDataset res = bean.updateSpInfoCS(params);
        return res;
    }
}
