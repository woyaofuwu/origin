
package com.asiainfo.veris.crm.order.soa.person.busi.reserveinfo;

import org.apache.log4j.Logger;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class CustomerReserveInfoBean extends CSBizBean {
    private static transient Logger log = Logger.getLogger(Dao.class);   
    
    /**
     * 查询客户预留信息
     */
    public IDataset queryCustomerReserveInfo(IData userInfo, Pagination pagination) throws Exception {
        IDataset whiteList = Dao.qryByCodeParser("TD_B_POSTCARD_INFO", "SEL_BY_SERIAL_NUMBER", userInfo, pagination, Route.getCrmDefaultDb());
        return whiteList;
    }
   
}
