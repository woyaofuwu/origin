package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporderoper;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TpOrderOperBean extends CSBizBean {

    //新增方法
    public boolean insertOrderOper(IData param) throws Exception{

        boolean result= Dao.insert("TP_ORDER_OPER", param);
        return result;
    }

}
