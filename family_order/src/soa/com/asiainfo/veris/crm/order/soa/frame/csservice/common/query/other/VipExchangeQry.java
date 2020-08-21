
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class VipExchangeQry
{

    /**
     * 查询用户当前有效的兑换记录
     * 
     * @param userId
     * @return
     * @throws Exception
     * @author zhouwu
     * @date 2014-07-07 10:38:37
     */
    public static IDataset getVipExchangeGifts(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_VIPEXCHANGE_BY_USERID", param);
    }

}
