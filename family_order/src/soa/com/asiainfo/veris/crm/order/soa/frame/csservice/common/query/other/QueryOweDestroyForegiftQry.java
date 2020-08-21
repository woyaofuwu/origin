
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryOweDestroyForegiftQry extends CSBizBean
{
    public static IDataset queryOweDestroyForegift(String areaCode, String startDate, String endDate) throws Exception
    {
        IData params = new DataMap();
        params.put("REMOVE_TAG", "4");
        params.put("AREA_CODE", areaCode);
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);
        // 用户服务状态		,Route.CONN_CRM_LXF
        params.put("USER_SVC_STATE", "9");
        //TODO huanghua 21 业务涉及超时管理，暂时先用crm或jour代替，测试环境重测---sql不涉及超时，不需要超时管理
//        return Dao.qryByCode("TF_F_USER", "SEL_OWEFEEUSER_FOREGIFT", params,Route.CONN_CRM_LXF);
        return Dao.qryByCode("TF_F_USER", "SEL_OWEFEEUSER_FOREGIFT", params,Route.getCrmDefaultDb());

    }

}
