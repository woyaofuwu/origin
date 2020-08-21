
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ChangePhonePhoneGenScoreHisIntfSVC extends CSBizService
{

    public IDataset changePhoneGenScoreHis(IData input) throws Exception
    {

        ChangePhonePhoneGenScoreHisIntfBean bean = (ChangePhonePhoneGenScoreHisIntfBean) BeanManager.createBean(ChangePhonePhoneGenScoreHisIntfBean.class);

        return bean.changePhoneGenScoreHis(input);
    }

    public void setTrans(IData input) throws Exception
    {
        // 没有传SERIAL_NUMBER，必须进行转换
        if ("SS.ChangePhonePhoneGenScoreHisIntfSVC.changePhoneGenScoreHis".equals(getVisit().getXTransCode()))
        {
            String new_sn = input.getString("ROUTEVALUE", "");
            if (new_sn != null && !"".equals(new_sn))
            {
                input.put("SERIAL_NUMBER", new_sn);
            }
        }
    }

}
