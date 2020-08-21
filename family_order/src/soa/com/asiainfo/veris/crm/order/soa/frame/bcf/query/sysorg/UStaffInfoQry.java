
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

public final class UStaffInfoQry
{
    /**
     * 根据客户经理标识查询客户经理名称
     * 
     * @param custMangerId
     * @return
     * @throws Exception
     */
    public static String getCustManageNameByCustManagerId(String custMangerId) throws Exception
    {
        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(),  Route.CONN_SYS, "TF_F_CUST_MANAGER_STAFF", "CUST_MANAGER_ID", "CUST_MANAGER_NAME", custMangerId);
    }

    /**
     * 根据客户经理标识查询客户经理手机号码
     * 
     * @param custMangerId
     * @return
     * @throws Exception
     */
    public static String getCustManageSnByCustManagerId(String custMangerId) throws Exception
    {
        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(),  Route.CONN_SYS, "TF_F_CUST_MANAGER_STAFF", "CUST_MANAGER_ID", "SERIAL_NUMBER", custMangerId);
    }

    /**
     * 根据员工标识查询员工归属部门
     * 
     * @param staffId
     * @return
     * @throws Exception
     */
    public static String getStaffDepartIdByStaffId(String staffId) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "DEPART_ID", staffId);
        }

        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "DEPART_ID", staffId);
    }

    /**
     * 根据员工标识查询员工名称
     * 
     * @param staffId
     * @return
     * @throws Exception
     */
    public static String getStaffNameByStaffId(String staffId) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", staffId);
        }

        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", staffId);
    }

    /**
     * 根据员工标识查询员工联系方式
     * 
     * @param staffId
     * @return
     * @throws Exception
     */
    public static String getStaffSnByStaffId(String staffId) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "SERIAL_NUMBER", staffId);
        }

        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "SERIAL_NUMBER", staffId);
    }

    /**
     * 根据客户经理标识查询客户经理配置信息
     * 
     * @param custMangerId
     * @return
     * @throws Exception
     */
    public static IData qryCustManagerInfoByCustManagerId(String custManagerId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("CUST_MANAGER_ID", custManagerId);

        IDataset ids = Dao.qryByCode("TF_F_CUST_MANAGER_STAFF", "SEL_BY_PK", inparam, Route.CONN_CRM_CEN);

        return IDataUtil.isNotEmpty(ids) ? ids.getData(0) : new DataMap();
    }

    /**
     * 根据员工标识查询客户经理配置信息
     * 
     * @param custManagerId
     * @return
     * @throws Exception
     */
    public static IDataset qryCustManagerStaffById(String custManagerId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("CUST_MANAGER_ID", custManagerId);

        return Dao.qryByCode("TF_F_CUST_MANAGER_STAFF", "SEL_BY_PK", inparam, Route.CONN_CRM_CEN);
    }

    /**
     * 根据手机号码获得客户经理信息
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset qryCustManagerStaffBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_F_CUST_MANAGER_STAFF", "SEL_BY_SN", param, Route.CONN_SYS);
    }

    /**
     * 根据员工工号查询员工配置信息
     * 
     * @param staffId
     * @return
     * @throws Exception
     */
    public static IData qryStaffAreaInfoByStaffId(String staffId) throws Exception
    {
        IData data = UStaffInfoQry.qryStaffInfoByPK(staffId);

        if (IDataUtil.isEmpty(data))
        {
            return data;
        }

        // 业务区编码
        String cityCode = data.getString("CITY_CODE");

        // 业务区名称
        String areaName = UAreaInfoQry.getAreaNameByAreaCode(cityCode);

        IData map = new DataMap();

        map.put("STAFF_NAME", data.getString("STAFF_NAME", ""));
        map.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        map.put("AREA_NAME", areaName);

        return map;
    }

    /**
     * 根据员工标识查询员工配置信息
     * 
     * @param staffId
     * @return
     * @throws Exception
     */
    public static IData qryStaffInfoByPK(String staffId) throws Exception
    {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);

        IDataset ids = Dao.qryByCode("TD_M_STAFF", "SEL_BY_PK", param, Route.CONN_SYS);

        return IDataUtil.isNotEmpty(ids) ? ids.getData(0) : new DataMap();
    }

    /**
     * 根据员工手机号码查询员工配置信息
     * 
     * @param staffId
     * @return
     * @throws Exception
     */
    public static IDataset qryStaffInfoBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TD_M_STAFF", "SEL_BY_NUMBER", param, Route.CONN_SYS);
    }
}
