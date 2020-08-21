package com.asiainfo.veris.crm.order.soa.person.busi.serviceMaintain;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.openAccountInfoQuery.OpenAccountInfoQueryBean;

public class ServiceMaintainSVC extends CSBizService{

    // 查询可操作的服务
    public  IDataset queryAvailableServices(IData input) throws Exception{

        ServiceMaintainBean bean = BeanManager.createBean(ServiceMaintainBean.class);
        IDataset idata = bean.queryAvailableServices();
        return idata;
    }

    // 导入基础功能服务信息
    public  IData importServiceInfo(IData input) throws Exception{

        ServiceMaintainBean bean = BeanManager.createBean(ServiceMaintainBean.class);
        IData idata = bean.importServiceInfo(input);
        return idata;
    }

    // 查询基础功能服务信息
    public IDataset queryBaseServiceInfo(IData input) throws Exception{

        ServiceMaintainBean bean = BeanManager.createBean(ServiceMaintainBean.class);
        IDataset idata = bean.queryBaseServiceInfo(input,getPagination());
        return idata;
    }
}
