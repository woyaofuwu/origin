package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class TpOrderBean extends CSBizBean {
    //新增方法
    public boolean insertOrder(IData param) throws Exception{
        boolean result=Dao.insert("TP_ORDER", param);
        return result;
    }

    public String getTpOrderId() throws Exception{
        String seq = SeqMgr.getTpOrderId();
        return seq;
    }

    //根据条件查询甩单信息
    public IDataset queryTpOrderInfos(IData data,Pagination pagination) throws Exception {
        return Dao.qryByCodeParser("TP_ORDER", "SEL_ORDER_INFO", data,pagination);
    }


    //根据工单号审核
    public int auditTpOrder(IData data) throws Exception {
        return Dao.executeUpdateByCodeCode("TP_ORDER", "UPD_AUDIT_ORDER_INFO", data);
    }

    //根据工单号直接归档
    public int archiveOrder(IData data) throws Exception {
        return Dao.executeUpdateByCodeCode("TP_ORDER", "UPD_ARCH_ORDER", data);
    }

    //根据工单号撤单
    public int cancelOrder(IData data) throws Exception {
        return Dao.executeUpdateByCodeCode("TP_ORDER", "UPD_CANCLE_ORDER", data);
    }


    //根据条件查询未审核甩单信息
    public IDataset queryUncheckOrder(IData data,Pagination pagination) throws Exception {
        return Dao.qryByCodeParser("TP_ORDER", "SEL_UNAUDIT_ORDER_INFO", data,pagination);
    }

    /**
     * 甩单主表状态修改
     * @param orderId
     * @param state
     */
    public void updateTpOrder(String orderId, String state) throws Exception{
        IData data = new DataMap();
        data.put("ORDER_ID",orderId);
        data.put("STATE",state);
        Dao.executeUpdateByCodeCode(TpConsts.TableName.Tp_Order,"UPDATE_TP_ORDER",data);
    }
}
