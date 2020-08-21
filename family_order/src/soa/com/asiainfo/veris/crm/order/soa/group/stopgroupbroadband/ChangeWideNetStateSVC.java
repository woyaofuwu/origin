
package com.asiainfo.veris.crm.order.soa.group.stopgroupbroadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.changeuserelement.ChangeWideNetSvcElement;

public class ChangeWideNetStateSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 创建集团宽带暂停和成员宽带暂停批量任务
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset StopOneKey(IData inParam) throws Exception
    {
        StopOneKeyBean bean = new StopOneKeyBean();

        return bean.crtBat(inParam);
    }
    /**
     * 创建集团宽带开机和成员宽带开机批量任务
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset OpenOneKey(IData inParam) throws Exception
    {
        OpenOneKeyBean bean = new OpenOneKeyBean();

        return bean.crtBat(inParam);
    }
    
    /**
     * 创建集团宽带停开机订单
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtTrade(IData inParam) throws Exception
    {
        ChangeWideNetSvcElement bean = new ChangeWideNetSvcElement();

        return bean.crtTrade(inParam);
    }
    
}
