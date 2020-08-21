
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ibossqryuserinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class IbossGetUserMpayQry
{

    /**
     * 取用户手机支付服务信息
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public static IDataset queryUserSvcId(IData idata) throws Exception
    {
        IDataset svc = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_BIZTYPEBIZ54_NEW", idata);
        return svc;
    }
}
