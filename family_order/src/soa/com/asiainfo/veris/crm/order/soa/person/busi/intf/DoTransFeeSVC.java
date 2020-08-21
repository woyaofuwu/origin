
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DoTransFeeSVC extends CSBizService
{

    /**
     * 飞豆充值请求信息插入临时表 add by lili 2013.7.11
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData addTransFeeTemp(IData inParam) throws Exception
    {
        DoTransFeeBean transFee = BeanManager.createBean(DoTransFeeBean.class);
        return transFee.insertTransFeeTemp(inParam);
    }

    /**
     * 飞豆充值请求信息查询
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData selTransFeeTemp(IData inParam) throws Exception
    {
        DoTransFeeBean transFee = BeanManager.createBean(DoTransFeeBean.class);
        return transFee.getTransFeeTemp(inParam);
    }

    /**
     * 飞豆充值请求信息修改
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData updTransFeeTemp(IData inParam) throws Exception
    {
        DoTransFeeBean transFee = BeanManager.createBean(DoTransFeeBean.class);
        return transFee.updTransFeeTemp(inParam);
    }
}
