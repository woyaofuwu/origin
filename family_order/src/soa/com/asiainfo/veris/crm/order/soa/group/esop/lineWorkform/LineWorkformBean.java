
package com.asiainfo.veris.crm.order.soa.group.esop.lineWorkform;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class LineWorkformBean extends GroupBean
{

    /*
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */

    public static IDataset qryLineWorkformByCondition(IData param, Pagination pagination) throws Exception
    {
     // 根据客服姓名查询客户STAFF_ID,如果输入了客户姓名查询
        String staffName = param.getString("STAFF_NAME");
        String staffId = param.getString("STAFF_ID");
        if (StringUtils.isBlank(staffId) && StringUtils.isNotBlank(staffName))
        {
            IDataset staffList = StaticUtil.getList(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_NAME", "STAFF_ID", new String[]
            { "STAFF_NAME" }, new String[]
            { staffName });
            if (IDataUtil.isNotEmpty(staffList))
            {
                if (staffList.size() > 1)
                {
                    CSAppException.apperr(GrpException.CRM_GRP_713, "查询到有多个工号的客户经理姓名为【" + staffName + "】，请输入工号后重试！");
                }
                else
                {
                    staffId = (String) staffList.get(0, "STAFF_ID");
                    param.put("STAFF_ID", staffId);
                }
            }
            else
            {
                CSAppException.apperr(GrpException.CRM_GRP_713, "根据客户经理姓名【" + staffName + "】未获取到任何信息，请确认该客户经理是否存在！");
            }
        }
        SQLParser sql = new SQLParser(param);
        // 工单状态 未完成0、已完成9、已挂起2 -Q、已撤销4 C
        String state = param.getString("RESULT_STATE");
        if ("2".equals(state))
        {
            param.put("RESULT_STATE", "Q");
        }
        else
        {
            param.put("RESULT_STATE", null);
            param.put("STATE", state);
        }
        sql.addSQL("SELECT T.PRODUCT_NO,T.TRADENAME,T.IBSYSID,T.TITLE,T.GROUP_ID,T.CUST_NAME,T.CITY_CODE,T.MANAGER_STAFF_ID,T.ACCEPT_STAFF_ID, ");
        sql.addSQL("T.SERIAL_NUMBER,T.PRODUCT_NAME,T.BPM_TEMPLET_ID,T.ACCEPTTANCE_PERIOD,T.BUSINESSTYPE,T.PROJECTNAME, ");
        sql.addSQL("T.CHANGEMODE,TO_CHAR(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.addSQL("TO_CHAR(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.addSQL("TO_CHAR(T.SUSPEND_START_DATE, 'yyyy-mm-dd hh24:mi:ss') SUSPEND_START_DATE, ");
        sql.addSQL("TO_CHAR(T.SUSPEND_END_DATE, 'yyyy-mm-dd hh24:mi:ss') SUSPEND_END_DATE, ");
        sql.addSQL("T.SUSPEND_TIME,T.OPEN_TIME,T.BANDWIDTH,T.NOTIN_RSRV_STR2,T.NOTIN_RSRV_STR3,T.NOTIN_RSRV_STR16, ");
        sql.addSQL("T.NOTIN_RSRV_STR10,T.NOTIN_RSRV_STR11,T.NOTIN_RSRV_STR12,T.BIZSECURITYLV,T.PORTACUSTOM, ");
        sql.addSQL("T.PROVINCEA,T.CITYA,T.AREAA,T.COUNTYA,T.VILLAGEA,T.PORTAINTERFACETYPE,T.PORTACONTACTPHONE, ");
        sql.addSQL("T.PORTACONTACT,T.PORTZCUSTOM,T.PROVINCEZ,T.CITYZ,T.AREAZ,T.COUNTYZ,T.VILLAGEZ, ");
        sql.addSQL("T.PORTZINTERFACETYPE,T.PORTZCONTACTPHONE,T.PORTZCONTACT,T.IPTYPE,T.CUSAPPSERVIPADDNUM, ");
        sql.addSQL("T.CUSAPPSERVIPV6ADDNUM,T.CUSAPPSERVIPV4ADDNUM,T.IPCHANGE ");
        sql.addSQL("FROM TF_B_EWE_LINEWORK_LIST T ");
        sql.addSQL("WHERE 1=1 ");
        sql.addSQL("AND (:PRODUCT_NO IS NULL OR T.PRODUCT_NO=:PRODUCT_NO) ");
        sql.addSQL("AND (:GROUP_ID IS NULL OR T.GROUP_ID=:GROUP_ID) ");
        sql.addSQL("AND (:STAFF_ID IS NULL OR T.MANAGER_STAFF_ID=:STAFF_ID) ");
        sql.addSQL("AND (:CITY_CODE is null or T.CITY_CODE=:CITY_CODE) ");
        // 专线类型
        sql.addSQL("AND (:BUSI_CODE IS NULL OR T.PRODUCT_ID=:BUSI_CODE) ");
        sql.addSQL("AND (:RESULT_STATE IS NULL OR T.BUSI_STATE=:RESULT_STATE) ");
        sql.addSQL("AND (:STATE IS NULL OR T.RSRV_STR1=:STATE) ");
        // 业务类型
        sql.addSQL("AND (:BPM_TEMPLET_ID IS NULL OR T.BPM_TEMPLET_ID=:BPM_TEMPLET_ID) ");
        // 经历时长
        sql.addSQL("AND (:MAX_DATE  IS NULL OR T.OPEN_TIME=:MAX_DATE) ");
        sql.addSQL(" AND (:START_DATE IS NULL OR to_char(T.START_DATE, 'yyyymmddss') >= to_char(to_date(:START_DATE, 'yyyy-mm-dd'), 'yyyymmddss')) ");
        sql.addSQL(" AND (:END_DATE IS NULL OR to_char(T.END_DATE, 'yyyymmddss') <= to_char(to_date(:END_DATE, 'yyyy-mm-dd'), 'yyyymmddss')) ");
        sql.addSQL("ORDER BY T.IBSYSID DESC ");
        IDataset rest = Dao.qryByParse(sql, pagination, Route.getJourDb(Route.CONN_CRM_CG));
        queryDetailValue(rest);

        return rest;
    }

    public static void queryDetailValue(IDataset rest) throws Exception
    {
        if (IDataUtil.isNotEmpty(rest))
        {
            for (int i = 0, restsize = rest.size(); i < restsize; i++)
            {
                IData data = rest.getData(i);
                String busiCode = data.getString("PRODUCT_ID");
                String businessType = data.getString("BUSINESSTYPE");
                String acceptancePeriod = data.getString("ACCEPTTANCE_PERIOD");
                String managerStaffId = data.getString("MANAGER_STAFF_ID");
                String acceptStaffId = data.getString("ACCEPT_STAFF_ID");
                String managerStaffName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", managerStaffId);
                data.put("MANAGER_STAFF_NAME", managerStaffName);
                String acceptStaffName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", acceptStaffId);
                data.put("ACCEPT_STAFF_NAME", acceptStaffName);
                String cityCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
                { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[]
                { "EOP_CUST_CITY_CODE", data.getString("CITY_CODE") });
                String bpmTempletId = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
                { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[]
                { "EOP_LINEWORKFORM_TEMPLET_ID", data.getString("BPM_TEMPLET_ID") });
                data.put("CITY_CODE", cityCode);
                data.put("BPM_TEMPLET_ID", bpmTempletId);
                // 转换业务类型编码，存入返回值集合
                if ("7012".equals(busiCode))
                {
                    data.put("BUSI_NAME", "数据专线");
                }
                else if ("7011".equals(busiCode))
                {
                    data.put("BUSI_NAME", "互联网专线");
                }
                else if ("7010".equals(busiCode))
                {
                    data.put("BUSI_NAME", "VOIP专线");
                }
                // 转换项目类型编码 BUSINESSTYPE
                if ("1".equals(businessType))
                {
                    data.put("BUSINESSTYPE", "零星");
                }
                else if ("0".equals(businessType))
                {
                    data.put("BUSINESSTYPE", "项目");
                }
                // 转换计费方式编码 ACCEPTTANCE_PERIOD
                if ("0".equals(acceptancePeriod))
                {
                    data.put("ACCEPTTANCE_PERIOD", "立即计费");
                }
                else if ("1".equals(acceptancePeriod))
                {
                    data.put("ACCEPTTANCE_PERIOD", "下账期计费");
                }
                else if ("2".equals(acceptancePeriod))
                {
                    data.put("ACCEPTTANCE_PERIOD", "下下账期计费");
                }
            }
        }
    }

}
