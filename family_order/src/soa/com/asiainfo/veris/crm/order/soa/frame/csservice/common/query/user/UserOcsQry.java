
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: UserOcsQry.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: sunxin
 * @date: 2014-9-17 下午7:27:45 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-9-17 sunxin v1.0.0 修改原因
 */
public class UserOcsQry
{

    public static IDataset getUserOcsByUserid(String user_id, String biz_type) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("BIZ_TYPE", biz_type);
        return Dao.qryByCode("TF_F_USER_OCS", "SEL_BY_USERID", param);
    }
    
    
    public static IDataset getUserOcsByUseridDate(String user_id, String timePoint) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("TIME_POINT", timePoint);
        return Dao.qryByCode("TF_F_USER_OCS", "SEL_BY_UID_DATE", param);
    }
    
}
