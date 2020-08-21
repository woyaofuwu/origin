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
 * @ClassName: OfferQry.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-9 上午11:35:48 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-9 chengxf2 v1.0.0 修改原因
 */

public class OfferQry
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-24 下午04:30:18 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-24 chengxf2 v1.0.0 修改原因
     */
    public static IData getProductRoleByPK(String productId, String roleId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("ROLE_CODE", roleId);
        IDataset result = Dao.qryByCode("TD_B_PRODUCT_ROLE", "SEL_BY_PK", data, Route.CONN_CRM_CEN);
        return result.isEmpty() ? null : result.getData(0);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-9 上午11:36:11 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-9 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getProductRoleList(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_B_PRODUCT_ROLE", "SEL_ROLE_LIST", data, Route.CONN_CRM_CEN);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-22 下午03:28:20 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-22 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getRolePackageList(String productId, String roleId, String kindId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("ROLE_CODE", roleId);
        data.put("RELAT_KIND_ID", kindId);
        return Dao.qryByCode("TD_B_ROLE_PACKAGE", "SEL_PACK_LIST", data, Route.CONN_CRM_CEN);
    }
}
