
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class MobilePaymentQry
{

    /**
     * 查询手机支付交易记录
     * 
     * @param serialNubmer
     * @param cancelTag
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryAccountPay(String serialNubmer, String cancelTag, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNubmer);
        param.put("CANCEL_TAG", cancelTag);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_MOBILEPAY_BY_COND", param, pagination);
        return dataset;
    }

}
