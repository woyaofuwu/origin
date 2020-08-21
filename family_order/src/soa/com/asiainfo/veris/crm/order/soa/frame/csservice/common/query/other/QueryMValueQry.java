
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryMValueQry extends CSBizBean
{
    public static IDataset queryCycleList() throws Exception
    {
        IData indata = new DataMap();
        return Dao.qryByCode("TD_B_CYCLE", "SEL_CYCLE_NEW", indata, Route.CONN_CRM_CEN);
    }

    /**
     * 是否有打印权限
     */
    public IDataset isPrint(String staffId, String eparchyCode) throws Exception
    {

        IData param = new DataMap();
        SQLParser parser = new SQLParser(param);
        param.put("EPARCHY_CODE", eparchyCode);
        parser.addSQL("select count(1) THECOUNT from td_m_funcright t1 ,tf_m_stafffuncright t2 ");
        parser.addSQL("where t1.right_code=t2.right_code and t2.staff_id= '" + staffId + "'");
        parser.addSQL("and t1.right_code='QueryUserMvalue' ");
        return Dao.qryByParse(parser, Route.CONN_SYS);
    }
}
