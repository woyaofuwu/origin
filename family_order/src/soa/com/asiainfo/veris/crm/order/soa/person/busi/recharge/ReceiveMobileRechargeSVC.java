
package com.asiainfo.veris.crm.order.soa.person.busi.recharge;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ReceiveMobileRechargeSVC extends CSBizService
{
    /**
     * @Function: receiveMobileRecharge
     * @Description: 10086热线充值
     * @param param
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月28日 下午4:33:24
     */
    public IDataset receiveMobileRecharge(IData param) throws Exception
    {
        ReceiveMobileRechargeBean bean = BeanManager.createBean(ReceiveMobileRechargeBean.class);
        IDataset result=new DatasetList();
        result.add(bean.receiveMobileRecharge(param));
        return result;
    }

}
