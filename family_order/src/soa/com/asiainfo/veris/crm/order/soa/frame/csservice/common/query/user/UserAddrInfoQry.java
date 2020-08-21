
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserAddrInfoQry
{

    /**
     * 通过USER_ID查询宽带用户安装地址
     * 
     * @author chenzm
     * @param userId
     * @return IDataset
     * @throws Exception
     */
    public static IDataset qryAddrByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_ADDR", "SEL_BY_PK", param);
    }
}
