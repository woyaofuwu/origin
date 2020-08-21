
package com.asiainfo.veris.crm.order.soa.person.busi.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ConstructionAddrSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IData constructionAddrDeal(IData data) throws Exception
    {
        IData result = new DataMap();
        result = upConstructionAddr(data);

        IData returnData = new DataMap();
        returnData.put("X_CHECK_INFO", "Trade OK!");
        returnData.put("X_CHECK_TAG", "0");
        returnData.putAll(result);
        return returnData;
    }
    
    public IData upConstructionAddr(IData data) throws Exception
    {
    	ConstructionAddrBean bean = (ConstructionAddrBean) BeanManager.createBean(ConstructionAddrBean.class);
        bean.checkUPValue(data);
        return bean.upConstructionAddr(data);
    }

}
