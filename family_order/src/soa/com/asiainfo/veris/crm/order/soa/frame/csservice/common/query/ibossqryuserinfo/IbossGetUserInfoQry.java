
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ibossqryuserinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class IbossGetUserInfoQry
{

    /**
     * 找出所有的地州配置iboss的
     * 
     * @return
     * @throws Exception
     */
    public static IDataset getEparchys() throws Exception
    {
        IData idata = new DataMap();
        idata.put("TYPE_ID", "IBOSS_ROUTE");
        IDataset eparchys = Dao.qryByCode("TD_S_STATIC", "SEL_BY_TYPEID", idata, Route.CONN_CRM_CEN);
        return eparchys;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getStaffInfo(IData data) throws Exception
    {
        IDataset staff = Dao.qryByCode("TD_M_STAFF", "SEL_ALL_BY_PK", data);
        return staff;
    }

    /**
     * vip等级
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getVipClass(IData data) throws Exception
    {
        IDataset svc = Dao.qryByCode("TD_M_VIPCLASS", "SEL_BY_PK", data);
        return svc;
    }

    /**
     * 查询大客户信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getVipsInfo(IData data) throws Exception
    {
        IDataset users = Dao.qryByCodeAllCrm("TF_F_CUST_VIP", "SEL_BY_VNO", data, false);
        // IDataset users = Dao.qryByCode("TF_F_CUST_VIP","SEL_BY_VNO", data);
        return users;
    }

}
