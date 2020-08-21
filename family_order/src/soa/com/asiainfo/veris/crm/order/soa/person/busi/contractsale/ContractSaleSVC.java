
package com.asiainfo.veris.crm.order.soa.person.busi.contractsale;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ContractSaleSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-29 上午11:06:45 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-29 chengxf2 v1.0.0 修改原因
     */
    public IData checkResInfo(IData input) throws Exception
    {
        ContractSaleBean bean = BeanManager.createBean(ContractSaleBean.class);
        return bean.checkResInfo(input);
    }
}
