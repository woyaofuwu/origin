
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CreateRedMemberBean extends CSBizBean
{

    static transient final Logger logger = Logger.getLogger(CreateRedMemberBean.class);

    public void createRedMember(IDataset data) throws Exception
    {
        Dao.insert("TF_F_SMS_REDMEMBER", data);
    }

    public int delRedMember(String sn, String endTime) throws Exception
    {
        endTime = endTime + SysDateMgr.END_DATE;
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", sn);
        data.put("END_TIME", endTime);

        int info = Dao.executeUpdateByCodeCode("TF_F_SMS_REDMEMBER", "DEL_USER", data);

        return info;
    }

    public int InsertBlackUser(IData data) throws Exception
    {
        int info = Dao.executeUpdateByCodeCode("TL_B_BLACKUSER", "INS_BLACK_USER", data, Route.CONN_CRM_CEN);

        return info;
    }

    public int updateBlackUser(IData data) throws Exception
    {
        int info = Dao.executeUpdateByCodeCode("TL_B_BLACKUSER", "UPD_BLACK_USER", data, Route.CONN_CRM_CEN);

        return info;
    }

    public int updateExitBlackUser(IData data) throws Exception
    {
        int info = Dao.executeUpdateByCodeCode("TL_B_BLACKUSER", "UPD_BLACK_EXIT", data, Route.CONN_CRM_CEN);

        return info;
    }
}
