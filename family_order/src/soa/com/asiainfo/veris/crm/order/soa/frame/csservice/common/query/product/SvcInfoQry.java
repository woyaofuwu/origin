
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.FillUserElementInfoUtil;

public class SvcInfoQry
{
	
    /**
     * 查询所有有效服务元素
     * 
     * @param data
     * @return
     * @throws Exception
     * @author zhuyu
     * @date 2014-4-03
     */
    public static IDataset getAllServiceInfo() throws Exception
    {
        return Dao.qryByCode("TD_B_SERVICE", "SEL_FOR_COMBOBOX", null, Route.CONN_CRM_CEN);
    }

    /**
     * 根据SERVICE_ID查询服务信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getDigitalService(String productId, String userId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("USER_ID", userId);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_SERVICE", "SEL_DIGITAL_SERVICE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getSvcByForcetag(String product_id, String trade_staff_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("TRADE_STAFF_ID", trade_staff_id);
        return Dao.qryByCodeParser("TD_B_SERVICE", "SEL_BY_IP_FORCETAG", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getSvcByIPNameCode(String product_id, String trade_staff_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("TRADE_STAFF_ID", trade_staff_id);
        return Dao.qryByCodeParser("TD_B_SERVICE", "SEL_BY_IPNAME_CODE", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getSvcByProudctId(String proudctId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", proudctId);

        return Dao.qryByCode("TD_B_SERVICE", "SEL_BY_PRODUCTID", param);
    }

    public static IDataset getSvcByUserIdandServiceId(String userId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);

        IDataset userSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_SVCID", param);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userSvcs, param, null);
        
        return userSvcs;
    }

    /**
     * 查询成员订购的服务
     * 
     * @param user_id
     * @param user_id_a
     * @param service_id
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getUserSvcInfo(String user_id, String user_id_a, String service_id, Pagination pagination) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        data.put("USER_ID_A", user_id_a);
        data.put("SERVICE_ID", service_id);

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" select * FROM TF_F_USER_SVC us ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" and us.user_id = to_number(:USER_ID) ");
        parser.addSQL(" and us.user_id_a = to_number(:USER_ID_A) ");
        parser.addSQL(" AND us.PARTITION_ID=MOD(to_number(:USER_ID), 10000) ");
        parser.addSQL(" AND START_DATE<=SYSDATE ");
        parser.addSQL(" AND END_DATE>SYSDATE ");
        parser.addSQL(" AND us.service_id=:SERVICE_ID");

        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryByPidPkgTypeCode(String productId, String pkgTypeCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_TYPE_CODE", pkgTypeCode);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_SERVICE", "SEL_BY_SERVICE_ELEMENT", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryAllServices() throws Exception
    {
        IData param = new DataMap();
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM TD_B_SERVICE A");
        parser.addSQL(" WHERE SYSDATE BETWEEN START_DATE AND END_DATE");
        parser.addSQL(" AND NOT EXISTS (");
        parser.addSQL(" SELECT 1 FROM td_b_attr_itema B");
        parser.addSQL(" WHERE A.SERVICE_ID = B.ID");
        parser.addSQL(" AND B.ID_TYPE = 'S')");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryByPkgId(String packageId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PACKAGE_ID", packageId);
        return Dao.qryByCode("TD_B_SERVICE", "SEL_BY_PACKID", cond, Route.CONN_CRM_CEN);
    }

    public static String queryMainTagByPackageIdAndServiceId(String productId, String packageId, String serviceId) throws Exception
    {
        // 目前主体服务只会在构成里面
        if (!StringUtils.equals("0", packageId))
        {
            return "0";
        }
        if (StringUtils.isBlank(productId) || StringUtils.isBlank(serviceId))
        {
            return "0";
        }

        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);

        IData pkgElemInfoData = UProductElementInfoQry.queryElementInfoByProductIdAndElementIdElemetnTypeCode(productId, serviceId, BofConst.ELEMENT_TYPE_CODE_SVC);

        if (IDataUtil.isNotEmpty(pkgElemInfoData))
        {
            return pkgElemInfoData.getString("MAIN_TAG", "0");
        }

        return "0";
    }

    /**
     * ADCMAS集团用户暂停还收取功能费的问题，根源在于ADCMAS集团用户开户时，部分用户登记的台账 tf_b_trade_svcstate 和tf_b_trade_svc 表，主体服务的MAIN_TAG错填字段为0,该方法通过
     * 配置主体的rsrv_str1和rsrv_str2同时为1，就登记main_tag=1。 2010-10-20 添加注释
     * 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static String queryMainTagServiceId(String serviceId) throws Exception
    {

        if (serviceId == null || "".equals(serviceId.trim()))
        {
            return "0";
        }

        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        IDataset idset = Dao.qryByCode("TD_B_SERVICE", "SEL_BY_SERVICEID", param, Route.CONN_CRM_CEN);
        String mainTag1 = (idset != null && idset.size() > 0) ? idset.getData(0).getString("RSRV_STR1", "") : "0";
        String mainTag2 = (idset != null && idset.size() > 0) ? idset.getData(0).getString("RSRV_STR2", "") : "0";
        String reuslt = "";
        // 集团用户欠费问题核查,主体服务存的登记在BUG，这次直接用TD_B_SERVICE表的RSRV_STR1和RSRV_STR2全为1时登记 main_tag=1
        if ("1".equals(mainTag1) && "1".equals(mainTag2))
        {
            reuslt = "1";
        }
        else
        {
            reuslt = "0";
        }
        return reuslt;
    }

    /**
     * 查询服务元素
     * 
     * @param data
     * @return
     * @throws Exception
     * @author awx
     * @date 2009-9-22
     */
    public static IDataset queryServiceByPk(String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);

        return Dao.qryByCode("TD_B_SERVICE", "SEL_BY_SERVICEID", param, Route.CONN_CRM_CEN);
    }

    /** 根据产品查对应的服务信息 */
    public static IDataset queryServices(IData param) throws Exception
    {

        // todo
        return Dao.qryByCode("TD_B_SERVICE", "SEL_BY_PRODUCTID", param);
    }

    /**
     * 查询服务列表
     * 
     * @param data
     * @return
     * @throws Exception
     * @author awx
     * @date 2009-6-23
     */
    public static IDataset queryServices(String serviceId, String serviceName, Pagination pagination) throws Exception
    {
        IData data = new DataMap();
        data.put("SERVICE_ID", serviceId);
        data.put("SERVICE_NAME", serviceName);
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" select s.service_id, s.service_name");
        parser.addSQL(" from td_b_service s");
        parser.addSQL(" where 1=1");
        parser.addSQL(" and s.service_id = :SERVICE_ID");
        parser.addSQL(" and s.service_name like '%' || :SERVICE_NAME || '%'");
        parser.addSQL(" and sysdate between s.start_date and s.end_date");
        parser.addSQL(" and (:TRADE_STAFF_ID='SUPERUSR' or exists" + " (select 1" + " from (select b.data_code" + " from tf_m_staffdataright a, tf_m_roledataright b" + " where" + " a.data_type = 'S'" + " and a.right_attr = 1"
                + " and a.right_tag = 1" + " and a.data_code = b.role_code" + " and a.staff_id = :TRADE_STAFF_ID" + " union" + " select a.data_code" + " from tf_m_staffdataright a" + " where" + " a.data_type = 'S'" + " and a.right_attr = 0"
                + " and a.right_tag = 1" + " and a.staff_id =:TRADE_STAFF_ID) tmp" + " where tmp.data_code = to_char(s.service_id)))");
        parser.addSQL(" and exists(" + " select 1 from td_s_commpara para" + " where 1=1" + " and para.param_attr = '991'" + " and para.para_code5 = '1'" + " and para.para_code1 = s.service_id" + ")");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }
    
    /***********************************************************************
     * 根据SERVICE_ID查询服务信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getServInfos(String serviceID) throws Exception
    {
        IData data = new DataMap();
        data.put("SERVICE_ID", serviceID);
        return Dao.qryByCode("TD_B_SERVICE", "SEL_BY_PK", data, Route.CONN_CRM_CEN);
    }
}
