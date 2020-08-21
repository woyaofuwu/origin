
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class UDepartInfoQry
{

    /**
     * 根据部门frame
     * 
     * @param departId
     * @return
     * @throws Exception
     */
    public static String getDepartFrameByDepartId(String departId) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_FRAME", departId);
        }

        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_DEPART", "DEPART_ID", "DEPART_FRAME", departId);
    }

    /**
     * 根据客户经理标识查询其归属部门/渠道
     * 
     * @param custManagerId
     * @return
     * @throws Exception
     */
    public static String getDepartIdByCustManagerId(String custManagerId) throws Exception
    {
        return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TF_F_CUST_MANAGER_STAFF", "CUST_MANAGER_ID", "DEPART_ID", custManagerId);
    }

    /**
     * 根据工号查询所属部门
     * 
     * @param staffId
     * @return
     * @throws Exception
     */
    public static String getDepartIdByStaffId(String staffId) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "DEPART_ID", "STAFF_ID", staffId);
        }

        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "DEPART_ID", "STAFF_ID", staffId);
    }

    /**
     * 根据部门/渠道编码查询部门/渠道名称
     * 
     * @param departId
     * @return
     * @throws Exception
     */
    public static String getDepartNameByDepartId(String departId) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", departId);
        }

        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", departId);
    }

    /**
     * 根据部门/渠道标识查询部门/渠道配置信息
     * 
     * @param departId
     * @return
     * @throws Exception
     */
    public static IData qryDepartByDepartId(String departId) throws Exception
    {
        IData param = new DataMap();
        param.put("DEPART_ID", departId);

        IDataset ids = Dao.qryByCode("TD_M_DEPART", "SEL_BY_PK", param, Route.CONN_SYS);

        if (IDataUtil.isEmpty(ids))
        {
            return new DataMap();
        }

        return ids.getData(0);
    }
    
    public static IDataset qryDepartByDepartKindCode(String departKindCode) throws Exception
    {
        IData param = new DataMap();
        param.put("DEPART_KIND_CODE", departKindCode);

        IDataset ids = Dao.qryByCode("TD_M_DEPART", "SEL_BY_DEPART_KIND_CODE", param, Route.CONN_SYS);

     

        return ids;
    }
}
