
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ForbiddenPointInfoQry
{

    public static IDataset queryForbiddenList(String inforecvid,String badnessSerial, String reportSerial, String type, String startDate, String endDate, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("INFO_RECV_ID", inforecvid);
        param.put("BADNESS_NUMBER", badnessSerial); // 受限主叫号码
        param.put("REPORT_NUMBER", reportSerial); // 举报号码
        param.put("OPERATE_TYPE", type); // 有效标志
        param.put("START_DATE", startDate); // 开始时间
        param.put("END_DATE", endDate); // 结束时间

        return Dao.qryByCodeParser("TF_F_FORBIDDEN_POINT", "SEL_BY_USER_FILTER", param, page, Route.CONN_CRM_CEN);
    }
}
