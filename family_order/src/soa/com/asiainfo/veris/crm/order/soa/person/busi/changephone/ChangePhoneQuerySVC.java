
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ChangePhoneQuerySVC extends CSBizService
{

    /**
     * 短信续约改号业务调用
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryAltSnInfo(IData input) throws Exception
    {
        ChangePhoneQueryBean bean = (ChangePhoneQueryBean) BeanManager.createBean(ChangePhoneQueryBean.class);
        return bean.queryAltSnInfo(input);
    }

    public void setTrans(IData input) throws Exception
    {
        // 没有传SERIAL_NUMBER，必须进行转换
        if ("SS.ChangePhoneQuerySVC.queryAltSnInfo".equals(getVisit().getXTransCode()))
        {
            String new_sn = input.getString("NEW_SN", "");
            if (new_sn != null && !"".equals(new_sn))
            {
                input.put("SERIAL_NUMBER", new_sn);
            }
        }
    }
}
