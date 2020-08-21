package com.asiainfo.veris.crm.order.soa.group.esop.transfer;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class QueryTransferWorkListBean extends GroupBean
{
    public static IDataset qryTransferInfosRecords(IData param, Pagination pagination) throws Exception
    {
    	param.put("IBSYS_ID", param.getString("cond_IBSYS_ID"));
        param.put("GROUP_ID", param.getString("cond_GROUP_ID"));
        SQLParser sql = new SQLParser(param);
        sql.addSQL("SELECT T.UPDATE_OLDCOLUMNVAL1 OLDSTAFF_ID,T.UPDATE_NEWCOLUMNVAL1 NEWSTAFF_ID,  ");
        sql.addSQL("TO_CHAR(T.ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
        sql.addSQL("IBSYS_ID,INFO_TOPIC,PRODUCT_NAME,GROUP_ID,CUST_NAME,NODE_NAME ");
        sql.addSQL("FROM TF_B_EOP_STAFFTRANSFERLOG T ");
        sql.addSQL("WHERE 1=1 ");
        sql.addSQL("AND (:OLDSTAFF_ID IS NULL OR T.UPDATE_OLDCOLUMNVAL1=:OLDSTAFF_ID) ");
        sql.addSQL("AND (:NEWSTAFF_ID IS NULL OR T.UPDATE_NEWCOLUMNVAL1=:NEWSTAFF_ID) ");
        sql.addSQL("AND (:IBSYS_ID IS NULL OR T.IBSYS_ID=:IBSYS_ID) ");
        sql.addSQL("AND (:GROUP_ID IS NULL OR T.GROUP_ID=:GROUP_ID) ");
        sql.addSQL("AND T.TYPE_ID = '0' ");
        sql.addSQL("ORDER BY T.ACCEPT_DATE DESC");
        IDataset rest = Dao.qryByParse(sql, pagination, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isNotEmpty(rest))
        {
            for (int i = 0, restsize = rest.size(); i < restsize; i++)
            {
                IData data = rest.getData(i);
                String oldStaffId = data.getString("OLDSTAFF_ID");
                String newStaffId = data.getString("NEWSTAFF_ID");
                String oldStaffName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", oldStaffId);
                String newStaffName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", newStaffId);
                data.put("OLDSTAFF_NAME", oldStaffName);
                data.put("NEWSTAFF_NAME", newStaffName);
            }
        }    
        return rest;
    }

}
