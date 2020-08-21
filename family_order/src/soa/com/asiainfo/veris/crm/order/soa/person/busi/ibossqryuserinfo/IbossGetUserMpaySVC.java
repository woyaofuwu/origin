
package com.asiainfo.veris.crm.order.soa.person.busi.ibossqryuserinfo;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class IbossGetUserMpaySVC extends CSBizService
{

    /**
     * 获取用户手机支付平台服务信息
     * 
     * @param pd
     * @param inparams
     *            IDVALUE
     * @return IDataset
     * @throws Exception
     */
    public IData getUserMpay(IData data) throws Exception
    {
        IbossGetUserMpayBean bean = BeanManager.createBean(IbossGetUserMpayBean.class);

        data.put("SERIAL_NUMBER", data.getString("IDVALUE"));
        return bean.getUserMpay(data);
    }
}
