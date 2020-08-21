
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossPayBiz;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BbossPayBizSVC extends CSBizService
{
    /**
     * chenyi 2014-7-14 反向订购流量叠加包
     */
    private static final long serialVersionUID = 1L;

    public static final IDataset crtBbossPayBiz(IData data) throws Exception
    {

        IDataset result = BbossPayBiz.crtBbossPayBiz(data);
        return result;

    }
    /**
     * chengjian
     * 2015-3-1
     * 开通工单反馈 
     * @param data
     * @return
     * @throws Exception
     */
    public static final IDataset bbossPayBizOrderOpenChkMeb(IData data) throws Exception
    {
        IDataset result = BbossPayBiz.bbossPayBizOrderOpenChkMeb(data);
        return result;
    }    
}
