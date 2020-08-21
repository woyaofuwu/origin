package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporderdetail;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TpOrderDetailSVC extends CSBizService {

    // 新增
    public boolean insertOrderDetail(IData param) throws Exception {
        TpOrderDetailBean tpOrderDetailBean = BeanManager.createBean(TpOrderDetailBean.class);
        boolean result = tpOrderDetailBean.insertOrderDetail(param);
        return result;
    }

    //批量新增
    public void batInsertOrderDetail(IDataset param) throws Exception {
        TpOrderDetailBean tpOrderDetailBean = BeanManager.createBean(TpOrderDetailBean.class);
        tpOrderDetailBean.batInsertOrderDetail(param);
    }

    //查询甩单详情表
    public IDataset queryTpOrderDetail(IData data) throws Exception
    {
        TpOrderDetailBean tpOrderDetailBean = BeanManager.createBean(TpOrderDetailBean.class);
        return tpOrderDetailBean.queryDetailByOrder(data);
    }
}
