
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UnicomTransferQry extends CSBizBean
{

    /**
     * 查询绑定联通电话查询
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset queryUnicomTransfer(IData params) throws Exception
    {
        return Dao.qryByCode("TI_B_TRANS_PHONE", "SEL_TRANS_PHONE", params);
    }
}
