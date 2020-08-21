
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserForegiftInfoQry
{
    /**
     * @Function: getUserForegift
     * @Description: 根据USER_ID查询用户押金
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-4-26 上午9:45:40 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 lijm3 v1.0.0 修改原因
     */
    public static IDataset getUserForegift(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_FOREGIFT", "SEL_BY_PK", param);
    }

    public static IDataset getUserForegift(String userId, String foreGiftCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("FOREGIFT_CODE", foreGiftCode);
        return Dao.qryByCode("TF_F_USER_FOREGIFT", "SEL_BY_USER_ID_FOREGIFT_CODE", param);
    }
}
