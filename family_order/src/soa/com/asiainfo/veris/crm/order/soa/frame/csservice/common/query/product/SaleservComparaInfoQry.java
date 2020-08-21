/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: SaleservComparaInfoQry.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-26 下午04:11:26 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-26 chengxf2 v1.0.0 修改原因
 */

public final class SaleservComparaInfoQry
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-26 下午04:14:05 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-26 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getSaleServComparaInfo(String paramValue, String paraCode1, String paraCode2, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("PARAM_VALUE", paramValue);
        data.put("PARA_CODE1", paraCode1);
        data.put("PARA_CODE2", paraCode2);
        data.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_SALESERV_COMMPARA", "SEL_BY_PK1", data, Route.CONN_CRM_CEN);
    }
}
