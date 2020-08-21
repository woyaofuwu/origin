
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class UAttrBizInfoQry
{

    /**
     * 查询产品/服务/资费控制信息
     * 
     * @param id
     * @param idType
     * @param attrObj
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getBizAttrByIdTypeObj(String id, String idType, String attrObj, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", idType);
        param.put("ATTR_OBJ", attrObj);

        return Dao.qryByCodeParser("TD_B_ATTR_BIZ", "SEL_BY_ID_TYPE_OBJ", param, pagination, Route.CONN_CRM_CEN);
    }

}
