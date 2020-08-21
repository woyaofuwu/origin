
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ChangePhoneReBookSVC extends CSBizService
{

    /**
     * 短信续约改号业务调用
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset reBookAltSnInfo(IData input) throws Exception
    {
        ChangePhoneReBookBean bean = (ChangePhoneReBookBean) BeanManager.createBean(ChangePhoneReBookBean.class);
        return bean.reBookAltSnInfo(input);
    }

    public void setTrans(IData input) throws Exception
    {
        // 没有传SERIAL_NUMBER，必须进行转换
        String new_sn = input.getString("NEW_SN", "");
        if ("SS.ChangePhoneReBookSVC.reBookAltSnInfo".equals(getVisit().getXTransCode()))
        {
            if (!"".equals(new_sn) && new_sn != null)
            {
                input.put("SERIAL_NUMBER", new_sn);
            }
        }
    }
}
