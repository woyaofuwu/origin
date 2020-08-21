
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryOneCardNCodesSIMSVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public IDataset querySimCardInfos(IData input) throws Exception
    {
        String num = input.getString("NUMBER");
        String type = input.getString("TYPE");

        QueryOneCardNCodesSIMBean bean = (QueryOneCardNCodesSIMBean) BeanManager.createBean(QueryOneCardNCodesSIMBean.class);
        return bean.querySimCardInfos(num, type);
    }
}
