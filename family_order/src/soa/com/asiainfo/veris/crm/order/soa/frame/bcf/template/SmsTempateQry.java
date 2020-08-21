
package com.asiainfo.veris.crm.order.soa.frame.bcf.template;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class SmsTempateQry
{
    public static IData qryTemplateContentByTempateId(String templateId) throws Exception
    {

        IData param = new DataMap();
        param.put("TEMPLET_CODE", templateId);
        param.put("SUB_SYS_CODE", "person");
        IDataset dataset = Dao.qryByCode("TD_B_SMS_TEMPLET", "SEL_BY_CODE_SUBSYS", param, Route.CONN_CRM_CEN);
        return dataset.size() > 0 ? dataset.getData(0) : null;
    }
}
