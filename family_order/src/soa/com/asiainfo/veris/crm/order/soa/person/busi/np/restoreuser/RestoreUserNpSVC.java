
package com.asiainfo.veris.crm.order.soa.person.busi.np.restoreuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class RestoreUserNpSVC extends CSBizService
{

    public IData checkResource(IData param) throws Exception
    {
        RestoreUserNpBean bean = BeanManager.createBean(RestoreUserNpBean.class);
        return bean.checkResource(param);
    }

    public IData checkSimReource(IData param) throws Exception
    {
        RestoreUserNpBean bean = BeanManager.createBean(RestoreUserNpBean.class);
        return bean.checkSimReource(param);
    }

    public IDataset getProductCatalog(IData param) throws Exception
    {
        RestoreUserNpBean bean = BeanManager.createBean(RestoreUserNpBean.class);
        return bean.getProductCatalog("0000");
    }

    public IDataset getProductFeeInfo(IData param) throws Exception
    {
        RestoreUserNpBean bean = BeanManager.createBean(RestoreUserNpBean.class);
        return bean.getProductFeeInfo(param);
    }

    public IData getUserNpInfos(IData param) throws Exception
    {
        RestoreUserNpBean bean = BeanManager.createBean(RestoreUserNpBean.class);

        IData m = new DataMap();
        m.put("resparam", bean.prepareForRes(param.getString("SERIAL_NUMBER"), param.getString("NET_TYPE_CODE"), param.getString("USER_ID")));
        m.put("info", bean.getUserNpInfos(param));
        m.put("productTypeList", bean.filterProductType("0000", param.getString("PRODUCT_ID")));
        m.put("resInfos", bean.getResInfos(param.getString("USER_ID")));
        return m;
    }

    public IDataset queryUserList(IData param) throws Exception
    {
        RestoreUserNpBean bean = BeanManager.createBean(RestoreUserNpBean.class);
        return bean.queryUserList(param);
    }
}
