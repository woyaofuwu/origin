
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.res;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ResParaInfoQry
{
    /**
     * 检查SIM卡是否是4GUSIM卡
     * 
     * @param simTypeCode
     *            --先从sim卡里取RES_TYPE_CODE然后把第一位去掉即为SIM_TYPE_CODE
     * @return
     * @throws Exception
     * @author sunxin
     */
    public static IDataset checkUser4GUsimCard(String simTypeCode) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("PARA_ATTR", "8510");
        qryParam.put("PARA_CODE1", "TD_LTE_SIM_CARDTYPE_CODE");
        qryParam.put("PARA_CODE2", simTypeCode);
        qryParam.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());

        return Dao.qryByCode("TD_M_ASSIGNPARA", "SEL_BY_USIMTYPECODE", qryParam, Route.CONN_CRM_CEN);
    }

    /**
     * @param paraValue
     * @param page
     * @return
     * @throws Exception
     * @author wangww
     */
    public static IDataset getResAreaInfo(String paraValue, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_VALUE1", paraValue);

        return Dao.qryByCode("TD_M_RES_PARA", "SEL_BY_LIMIT_CODE", param, page, Route.CONN_RES);
    }

    /**
     * @param param
     *            EPARCHY_CODE PARA_ATTR PARA_CODE1 PARA_CODE2
     * @return
     * @throws Exception
     */
    public static IDataset getResParaInfo(String eparchyCode, String paraAttr, String paraCode1, String paraCode2) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_CODE1", paraCode1);
        param.put("PARA_CODE2", paraCode2);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("PARA_ATTR", paraAttr);

        return Dao.qryByCode("TD_M_RES_PARA", "SEL_BY_PARACODE1_ANDCODE2", param, Route.CONN_RES);
    }

    public static String getResTypeNameByResTypeCode(String resTypeCode) throws Exception
    {
        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_RES, "RES_TYPE", "RES_TYPE_ID", "RES_TYPE_NAME", resTypeCode);
    }

    public static String getSwitchTypeBySwitchId(String switchId) throws Exception
    {
        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_RES, "RES_HLR", "HLR_CODE", "HLR_TYPE_CODE", switchId);
    }
}
