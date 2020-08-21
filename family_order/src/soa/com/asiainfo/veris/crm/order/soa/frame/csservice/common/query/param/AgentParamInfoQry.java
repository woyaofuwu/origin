
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class AgentParamInfoQry
{

    /**
     * 删除参数及子参数
     * 
     * @param data
     * @throws Exception
     */
    public static void deleteComparam(String param_attr, String eparchy_code, String param_id) throws Exception
    {

        StringBuilder sql = new StringBuilder();
        sql.append("delete td_s_agent_param");
        sql.append(" where param_id in(");
        sql.append(" select param_id from td_s_agent_param  where param_id = :PARAM_ID");
        sql.append(" union all");
        sql.append(" select param_id from td_s_agent_param");
        sql.append(" where param_attr = :PARAM_ATTR");
        sql.append(" and eparchy_code = :EPARCHY_CODE");
        sql.append(" start with param_id = :PARAM_ID");
        sql.append(" connect by prior param_value = parent_param_value)");

        IData data = new DataMap();
        data.put("PARAM_ATTR", param_attr);
        data.put("EPARCHY_CODE", eparchy_code);
        data.put("PARAM_ID", param_id);

        Dao.executeUpdate(sql, data);
    }

    /**
     * 删除参数
     * 
     * @param paramAttr
     * @param paramCode
     * @param paramValue
     * @param eparchyCode
     * @throws Exception
     */
    public static void deleteComparamById(String paramId) throws Exception
    {

        IData param = new DataMap();
        param.put("PARAM_ID", paramId);

        StringBuilder sql = new StringBuilder();
        sql.append("delete from td_s_agent_param");
        sql.append(" where param_id = :PARAM_ID");

        Dao.executeUpdate(sql, param);
    }

    /**
     * 获取代理商参数配置
     * 
     * @param data
     * @return
     * @throws Exception
     */
    /*
     * public static IDataset getCommpara(String paramAttr,String paramCode,String parentParamValue,String
     * paramValue,String eparchyCode, Pagination page) throws Exception { String paramAttr =
     * data.getString("PARAM_ATTR"); String paramCode = data.getString("PARAM_CODE"); String parentParamValue =
     * data.getString("PARENT_PARAM_VALUE"); String paramValue = data.getString("PARAM_VALUE"); String eparchyCode =
     * data.getString("EPARCHY_CODE"); return getCommpara(paramAttr, paramCode, parentParamValue, paramValue,
     * eparchyCode, page); }
     */
    public static IDataset getCommpara(String paramAttr, String paramCode) throws Exception
    {

        return getCommpara(paramAttr, paramCode, null, null, CSBizBean.getTradeEparchyCode());
    }

    public static IDataset getCommpara(String paramAttr, String paramCode, Pagination pagination) throws Exception
    {

        return getCommpara(paramAttr, paramCode, null, null, CSBizBean.getTradeEparchyCode(), pagination);
    }

    public static IDataset getCommpara(String paramAttr, String paramCode, String eparchyCode) throws Exception
    {

        return getCommpara(paramAttr, paramCode, null, null, eparchyCode);
    }

    public static IDataset getCommpara(String paramAttr, String paramCode, String eparchyCode, Pagination pagination) throws Exception
    {

        return getCommpara(paramAttr, paramCode, null, null, eparchyCode, pagination);
    }

    public static IDataset getCommpara(String paramAttr, String paramCode, String parentParamValue, String eparchyCode) throws Exception
    {

        return getCommpara(paramAttr, paramCode, parentParamValue, null, eparchyCode);
    }

    public static IDataset getCommpara(String paramAttr, String paramCode, String parentParamValue, String eparchyCode, Pagination pagination) throws Exception
    {

        return getCommpara(paramAttr, paramCode, parentParamValue, null, eparchyCode, pagination);
    }

    public static IDataset getCommpara(String paramAttr, String paramCode, String parentParamValue, String paramValue, String eparchyCode) throws Exception
    {

        return getCommpara(paramAttr, paramCode, parentParamValue, paramValue, eparchyCode, null);
    }

    /**
     * 获取代理商参数配置
     * 
     * @param paramAttr
     * @param paramCode
     * @param parentCodeValue
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommpara(String paramAttr, String paramCode, String parentParamValue, String paramValue, String eparchyCode, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARENT_PARAM_VALUE", parentParamValue);
        param.put("PARAM_VALUE", paramValue);
        param.put("EPARCHY_CODE", eparchyCode);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT param_id, param_attr,param_code,param_name,param_tag,parent_param_value,param_value,param_value2,param_value3,param_value4,param_value5,param_value6,param_value7,param_value8,param_value9,param_value10,order_code,");
        parser.addSQL(" to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,");
        parser.addSQL(" update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time");
        parser.addSQL(" FROM td_s_agent_param WHERE 1=1 ");
        // parser.addSQL(" AND param_tag = '1' ");
        parser.addSQL(" AND param_attr = :PARAM_ATTR");
        parser.addSQL(" AND param_code = :PARAM_CODE");
        parser.addSQL(" AND parent_param_value = :PARENT_PARAM_VALUE");
        if (parentParamValue == null || "".equals(parentParamValue))
        {
            parser.addSQL(" AND parent_param_value is null");
        }
        parser.addSQL(" AND param_value = :PARAM_VALUE");
        parser.addSQL(" AND (eparchy_code= :EPARCHY_CODE OR eparchy_code='ZZZZ')");
        parser.addSQL(" AND sysdate BETWEEN start_date AND end_date");
        parser.addSQL(" order by order_code,param_attr,param_code,param_value");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 通过id获取参数信息
     * 
     * @param Id
     * @return
     * @throws Exception
     */
    public static IData getCommparaById(String paramId) throws Exception
    {

        return Dao.qryByPK("TD_S_AGENT_PARAM", new String[]
        { "PARAM_ID" }, new String[]
        { paramId });
    }

    public static IDataset getComparamByParamValue(String paramAttr, String paramCode, String paramValue) throws Exception
    {

        return getCommpara(paramAttr, paramCode, null, paramValue, CSBizBean.getVisit().getStaffEparchyCode());
    }

    /**
     * 通过参数值获取参数配置
     * 
     * @param paramAttr
     * @param paramCode
     * @param paramValue
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getComparamByParamValue(String paramAttr, String paramCode, String paramValue, String eparchyCode) throws Exception
    {

        return getCommpara(paramAttr, paramCode, null, paramValue, eparchyCode);
    }

    public static IDataset getComparamByParentCodeValue(String paramAttr, String paramCode, String parentParamValue) throws Exception
    {

        return getCommpara(paramAttr, paramCode, parentParamValue, null, CSBizBean.getVisit().getStaffEparchyCode());
    }

    /**
     * 通过父参数值获取参数配置
     * 
     * @param parentCodeValue
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getComparamByParentCodeValue(String paramAttr, String paramCode, String parentParamValue, String eparchyCode) throws Exception
    {

        return getCommpara(paramAttr, paramCode, parentParamValue, null, eparchyCode);
    }

    public static IDataset queryAgentBelongInfo(String sn) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        return Dao.qryByCode("TD_M_STAFF", "SEL_BY_SN", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryAgentNameInfo(String agent_id) throws Exception
    {
        IData param = new DataMap();
        param.put("DEPART_ID", agent_id);
        return Dao.qryByCode("TD_M_DEPART", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryAgentSn(String agentSn) throws Exception
    {
        IData param = new DataMap();
        param.put("AGENT_SERIALNUMBER", agentSn);
        return Dao.qryByCode("TD_S_CPARAM", "CHECK_CHANNELSN", param, Route.CONN_CRM_CEN);
    }
}
