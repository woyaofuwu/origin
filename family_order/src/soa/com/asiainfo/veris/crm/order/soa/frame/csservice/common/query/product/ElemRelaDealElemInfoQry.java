
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ElemRelaDealElemInfoQry
{

    public static IDataset qryRelaDealInfoByElem(String elementTypeCode, String elementId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("ELEMENT_TYPE_CODE", elementTypeCode);
        param.put("ELEMENT_ID", elementId);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_ELEM_RELADEAL_ELEM", "SEL_BY_ELEM", param, Route.CONN_CRM_CEN);
    }
}
