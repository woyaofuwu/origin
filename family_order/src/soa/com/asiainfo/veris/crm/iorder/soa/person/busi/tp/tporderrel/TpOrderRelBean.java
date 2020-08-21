package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporderrel;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TpOrderRelBean extends CSBizBean {

    //批量新增方法
    public void batInsertOrderRel(IDataset param) throws Exception{
        Dao.insert("TP_ORDER_REL", param);
    }
}
