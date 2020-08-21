
package com.asiainfo.veris.crm.order.soa.person.busi.fastauth.fastauthapprove;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class FastAuthApproveSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    public IDataset approveAuth(IData input) throws Exception
    {
        FastAuthApproveBean bean = (FastAuthApproveBean) BeanManager.createBean(FastAuthApproveBean.class);
        IData data = new DataMap();
        IDataset dataset = new DatasetList();
        int updateSuccessFlag = bean.approveAuth(input, getPagination());
        data.put("UPDATE_SUCCESS_FLAG", updateSuccessFlag);
        dataset.add(data);
        return dataset;
    }

    public IDataset queryApplyTrade(IData input) throws Exception
    {
        FastAuthApproveBean bean = (FastAuthApproveBean) BeanManager.createBean(FastAuthApproveBean.class);
        return bean.queryApplyTrade(input, null);
    }

    public IDataset queryAuthTradeType(IData input) throws Exception
    {
        FastAuthApproveBean bean = (FastAuthApproveBean) BeanManager.createBean(FastAuthApproveBean.class);
        return bean.queryAuthTradeType(input, getPagination());
    }
}
