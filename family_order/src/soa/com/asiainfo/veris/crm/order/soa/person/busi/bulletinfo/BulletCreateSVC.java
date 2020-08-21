
package com.asiainfo.veris.crm.order.soa.person.busi.bulletinfo;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BulletCreateSVC extends CSBizService
{

    public IData createBullet(IData input) throws Exception
    {
        BulletCreateBean bean = BeanManager.createBean(BulletCreateBean.class);
        return bean.createBullet(input);
    }

}
