package com.asiainfo.veris.crm.order.soa.person.busi.reserveinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CustomerReserveInfoSVC extends CSBizService {

    private static final long serialVersionUID = 1L;

    public IDataset queryCustomerReserveInfo(IData userInfo) throws Exception {
        Pagination pagination = getPagination();
        CustomerReserveInfoBean bean = (CustomerReserveInfoBean) BeanManager.createBean(CustomerReserveInfoBean.class);
        return bean.queryCustomerReserveInfo(userInfo, pagination);
    }

}
