package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporderdetail;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TpOrderDetailBean extends CSBizBean {

    //新增方法
    public boolean insertOrderDetail(IData param) throws Exception{

        boolean result= Dao.insert("TP_ORDER_DETAIL", param);
        return result;
    }

    //批量新增方法
    public void batInsertOrderDetail(IDataset param) throws Exception{
        Dao.insert("TP_ORDER_DETAIL", param);
    }

    //根据工单号查询详情信息
    public IDataset queryDetailByOrder(IData data) throws Exception {
        return Dao.qryByCode("TP_ORDER_DETAIL", "SEL_BY_ORDER_ID", data);
    }
}
