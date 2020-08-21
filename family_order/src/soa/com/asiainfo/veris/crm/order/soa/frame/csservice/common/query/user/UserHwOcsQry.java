
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: UserHwOcsQry.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2013-7-25 下午7:27:45 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-7-25 lijm3 v1.0.0 修改原因
 */
public class UserHwOcsQry
{

    public static IDataset getUserHwOcsBySelbyUserid(String user_id, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TF_F_USER_HWOCS", "SEL_BY_USERID", param);
    }
}
