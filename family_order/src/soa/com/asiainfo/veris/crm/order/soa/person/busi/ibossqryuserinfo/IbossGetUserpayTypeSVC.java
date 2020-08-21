
package com.asiainfo.veris.crm.order.soa.person.busi.ibossqryuserinfo;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.plat.mobilepayment.AutoPayContractIntfBean;

public class IbossGetUserpayTypeSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -3563396865079182985L;

    /**
     * 付费类型查询
     * 
     * @param pd
     * @param param
     * @return 0 后付费 1 预付费
     * @throws Exception
     */
    public static IData getPayTypeQry(IData param) throws Exception
    {
        AutoPayContractIntfBean bean = BeanManager.createBean(AutoPayContractIntfBean.class);
        return bean.getPayTypeQry(param);
    }
}
