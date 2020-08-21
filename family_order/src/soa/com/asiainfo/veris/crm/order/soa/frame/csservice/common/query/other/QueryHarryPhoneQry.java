
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryHarryPhoneQry
{

    /**
     * @Function: getDayReportHarryPhones
     * @Description: 日报表
     * @param paraCode1
     * @param paraCode2
     * @param paraCode3
     * @param paraCode4
     * @param routeId
     * @param pagination
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月26日 上午9:33:57
     */
    public static IDataset getDayReportHarryPhones(String paraCode1, String paraCode2, String paraCode3, String paraCode4, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("PARA_CODE1", paraCode1);
        param.put("PARA_CODE2", paraCode2);
        param.put("PARA_CODE3", paraCode3);
        param.put("PARA_CODE4", paraCode4);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_HARRYPHONEREPORT", param, pagination);

    }

    /**
     * @Function: getHarryPhonesByNormal
     * @Description: 普通查询
     * @param paraCode1
     * @param paraCode2
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月26日 上午9:35:53
     */
    public static IDataset getHarryPhonesByNormal(String paraCode1, String paraCode2, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_CODE1", paraCode1);
        param.put("PARA_CODE2", paraCode2);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_HARRYPHONE", param, pagination);
    }

    /**
     * 骚扰电话查询
     * 
     * @param pd
     * @param cond
     * @return
     * @throws Exception
     */
    public static IDataset queryListByCodeCodeParser(IData cond, Pagination pagination) throws Exception
    {

        IDataset set = new DatasetList();

        String routeId = cond.getString(Route.ROUTE_EPARCHY_CODE);
        cond.put("PARA_CODE1", cond.getString("BEGIN_SN"));
        cond.put("PARA_CODE2", cond.getString("END_SN"));

        set = Dao.qryByCode("TL_B_HARRYPHONE", "SEL_BY_HARRYPHONE", cond, pagination, routeId);

        return set;

    }

}
