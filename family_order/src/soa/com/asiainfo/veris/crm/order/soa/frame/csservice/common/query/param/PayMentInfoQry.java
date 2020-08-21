
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PayMentInfoQry
{

    /**
     * 获取TD_B_PAYMENT中配置的数据
     * 
     * @author zhujm
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getPayment() throws Exception
    {
        // Dao.qryByTable方法暂无实现 未添加路由配置
        // IDataset tmp = Dao.qryByTable("TD_B_PAYMENT", "PAYMENT_ID", "PAYMENT");

        IDataset tmp = StaticUtil.getList(CSBizBean.getVisit(), "TD_B_PAYMENT", "PAYMENT_ID", "PAYMENT");

        tmp = tmp == null ? new DatasetList() : tmp;
        IDataset tmp2 = new DatasetList();

        for (int i = 0; i < tmp.size(); i++)
        {
            IData data = tmp.getData(i);
            IData temp = new DataMap();
            temp.put("CODE", data.getString("PAYMENT_ID").trim());
            temp.put("NAME", data.getString("PAYMENT").trim());
            tmp2.add(temp);
        }

        return tmp2;
    }

    /**
     * 查询用户是否定制或定制过手机缴费通
     * 
     * @param pd
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     * @author:chenzg
     * @date:2010-9-8
     */
    public static IDataset querySnPaymentInfo(IData inparams) throws Exception
    {
        inparams.put("MAKE_STATE", "0");
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_SERNUMBER_PAYMENT_INFO", inparams);
    }

}
