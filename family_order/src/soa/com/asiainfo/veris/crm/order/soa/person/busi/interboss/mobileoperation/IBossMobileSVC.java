
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class IBossMobileSVC extends CSBizService
{

    public IDataset getCustInfo(IData data) throws Exception
    {
        // String serialNumber = data.getString("MOBILENUM");
        // IData info = RouteInfoQry.getMofficeInfoBySn(serialNumber);
        // if(IDataUtil.isNotEmpty(info)) {
        // StringBuilder sb = new StringBuilder();
        // sb.append("您的手机[").append(serialNumber).append("]为本省用户,无法办理一级BOSS业务!");
        // CSAppException.apperr(CrmCommException.CRM_COMM_103, sb.toString());
        // }
        IBossMobileBean bean = BeanManager.createBean(IBossMobileBean.class);
        return bean.getCustInfo(data);
    }

    public IDataset loadPrintData(IData data) throws Exception
    {
        IBossMobileBean bean = BeanManager.createBean(IBossMobileBean.class);
        return bean.loadPrintData(data);
    }

    public IDataset openMobile(IData data) throws Exception
    {
        IBossMobileBean bean = BeanManager.createBean(IBossMobileBean.class);
        return bean.openMobile(data);
    }

    public IDataset stopMobile(IData data) throws Exception
    {
        IBossMobileBean bean = BeanManager.createBean(IBossMobileBean.class);
        return bean.stopMobile(data);
    }
}
