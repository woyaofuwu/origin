
package com.asiainfo.veris.crm.order.soa.group.esop.lineworkformdata;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class LineWorkformdataBean extends GroupBean
{

    /*	*//**
           * @param param
           * @param pagination
           * @return
           * @throws Exception
           */

    public static IDataset qryLineWorkformByCondition(IData param, Pagination pagination) throws Exception
    {
        // 根据客服姓名查询客户STAFF_ID,如果输入了客户姓名查询
        String staffName = param.getString("TRADE_STAFF_NAME");
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
        sql.addSQL("SELECT T.USER_ID,T.LINE_NAME,T.LINE_STLBUG,T.INSTANCE_NUMBER,T.BIZ_SECURITY_LV,T.PORT_CUSTOME_A, ");
        sql.addSQL("T.PROVINCE_A,T.CITY_A,T.AREA_A,T.COUNTY_A,T.VILLAGE_A,T.PORT_INTERFACE_TYPE_A, ");
        sql.addSQL("T.PORT_CONTACT_A,T.PORT_CONTACT_PHONE_A,T.PORT_CUSTOME_Z,T.PROVINCE_Z,T.STAFF_ID, ");
        sql.addSQL("T.CITY_Z,T.AREA_Z,T.COUNTY_Z,T.VILLAGE_Z,T.PORT_INTERFACE_TYPE_Z,T.PORT_CONTACT_Z,  ");
        sql.addSQL("T.PORT_CONTACT_PHONE_Z,T.BAND_WIDTH,T.IP_TYPE,T.CUST_APPSERV_IPADDNUM,T.IPV6_NUM, ");
        sql.addSQL("T.IPV4_NUM,T.CUST_NAME,T.CITY_CODE,T.GROUP_ID,T.SERV_LEVEL,T.SERIAL_NUMBER,T.SERIAL_NUMBER_B,T.PRODUCT_ID ");
        sql.addSQL("FROM TF_B_EWE_LINEWORKDATA_LIST T ");
        sql.addSQL("WHERE 1=1 ");
        sql.addSQL("  AND (:CITY_CODE is null or T.CITY_CODE=:CITY_CODE) ");
        sql.addSQL("  AND (:STAFF_ID is null or T.STAFF_ID=:STAFF_ID) ");
        sql.addSQL("  AND (:GROUP_ID is null or T.GROUP_ID=:GROUP_ID) ");
        sql.addSQL("  AND (:SERIAL_NUMBER is null or T.SERIAL_NUMBER=:SERIAL_NUMBER) ");
        sql.addSQL("  AND (:SERIAL_NUMBER_B is null or T.SERIAL_NUMBER_B=:SERIAL_NUMBER_B) ");
        sql.addSQL("  AND (:INSTANCE_NUMBER is null or T.INSTANCE_NUMBER=:INSTANCE_NUMBER) ");
        sql.addSQL("  AND (:CITY_CODE is null or T.CITY_CODE=:CITY_CODE) ");
        IDataset rest = Dao.qryByParse(sql, pagination, Route.CONN_CRM_CG);
        queryAttrValue(rest);
        return rest;
    }

    public static void queryAttrValue(IDataset rest) throws Exception
    {
        if (IDataUtil.isNotEmpty(rest))
        {
            for (int i = 0, restsize = rest.size(); i < restsize; i++)
            {
                IData data = rest.getData(i);
                String staffId = data.getString("STAFF_ID");
                String staffName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", staffId);
                data.put("STAFF_NAME", staffName);
                String cityCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
                { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[]
                { "EOP_CUST_CITY_CODE", data.getString("CITY_CODE") });
                data.put("CITY_CODE", cityCode);
                IData param = new DataMap();
                String userId = data.getString("USER_ID");
                param.put("USER_ID", userId);
                // 获得专线月租费、一次性费用、SLA服务费、IP地址使用费、软件应用服务费、技术支持服务费 的ATTR_VALUE值
                SQLParser sql = new SQLParser(param);
                // 查询取得对应参数的ATTR_VALUE值
                sql.addSQL("SELECT * FROM (SELECT T.ATTR_CODE, T.ATTR_VALUE FROM TF_F_USER_ATTR T ");
                sql.addSQL("WHERE T.USER_ID =:USER_ID AND T.END_DATE > SYSDATE) PIVOT(SUM(ATTR_VALUE) FOR ATTR_CODE IN ");
                // 专线月租费、一次性费用、SLA服务费、IP地址使用费、软件应用服务费、技术支持服务费
                sql.addSQL("('59701003'COL1,'59701004'COL2,'59701013'COL3,'59701007'COL4,'59701008'COL5,'59701009'COL6)) ");
                IDataset attrValue = Dao.qryByParse(sql, Route.CONN_CRM_CG);
                data.put("COL1", attrValue.getData(0).getString("COL1"));
                data.put("COL2", attrValue.getData(0).getString("COL2"));
                data.put("COL3", attrValue.getData(0).getString("COL3"));
                data.put("COL4", attrValue.getData(0).getString("COL4"));
                data.put("COL5", attrValue.getData(0).getString("COL5"));
                data.put("COL6", attrValue.getData(0).getString("COL6"));

                // 查询 客户级别、客户服务等级、客户经理姓名
                String groupId = data.getString("GROUP_ID");
                // 查询客户信息
                IData groupInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
                if (IDataUtil.isNotEmpty(groupInfo))
                {
                    String classId = groupInfo.getString("CLASS_ID");
                    String enterpriseType = groupInfo.getString("ENTERPRISE_TYPE_NAME");
                    // 客户等级
                    data.put("GROUP_CLASS", StaticUtil.getStaticValue("CUSTGROUP_CLASSID", classId));
                    // 行业属性
                    data.put("ENTERPRISE_TYPE_NAME", enterpriseType);
                }

                // 根据PRODUCT_ID查询专线类型
                String productId = data.getString("PRODUCT_ID");
                if ("97012".equals(productId))
                {
                    data.put("LINE_TYPE", "数据专线");
                }
                else if ("97011".equals(productId))
                {
                    data.put("LINE_TYPE", "互联网专线");
                }
                else if ("701001".equals(productId))
                {
                    data.put("LINE_TYPE", "VOIP专线");
                }else if ("97016".equals(productId))
                {
                    data.put("LINE_TYPE", "IMS专线");
                }else if ("970111".equals(productId))
                {
                    data.put("LINE_TYPE", "云互联（互联网）");
                }else if ("970112".equals(productId))
                {
                    data.put("LINE_TYPE", "云专线（互联网)");
                }else if ("970121".equals(productId))
                {
                    data.put("LINE_TYPE", "云互联（数据传输）");
                }else if ("970122".equals(productId))
                {
                    data.put("LINE_TYPE", "云专线（数据传输）");
                }

                // 根据SERV_LEVEL查询专线类型
                String servLevel = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
                { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[]
                { "CUSTGROUP_SERV_LEVEL", data.getString("SERV_LEVEL") });
                data.put("SERV_LEVEL", servLevel);

                // 查询计费生效时间 7010语音 7011互联网 7012
                if ("7010".equals(productId))
                {
                    String bilingDate = "";
                    if (StringUtils.isNotEmpty(userId))
                    {
                        IDataset discntDatas = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, EcConstants.ZERO_DISCNT_CODE);
                        if (IDataUtil.isNotEmpty(discntDatas))
                        {// 存在零元资费
                            String endDate = discntDatas.first().getString("END_DATE");
                            if (StringUtils.isNotEmpty(endDate))
                            {
                                bilingDate = SysDateMgr.getFirstDayOfNextMonth(endDate);
                            }
                        }
                        else
                        {// 不存在零元资费
                            discntDatas = UserDiscntInfoQry.getAllDiscntInfo(userId);
                            if (IDataUtil.isNotEmpty(discntDatas))
                            {
                                bilingDate = discntDatas.first().getString("START_DATE");
                            }
                        }
                        if (StringUtils.isNotBlank(bilingDate))
                        {
                            bilingDate = SysDateMgr.suffixDate(bilingDate, 0);
                            bilingDate = bilingDate.substring(0, 19);
                        }
                        data.put("BILING_DATE", bilingDate);
                    }
                }
                else
                {
                    IDataset grpProducts = UProductMebInfoQry.queryGrpProductInfosByMebProductId(productId);

                    if (IDataUtil.isNotEmpty(grpProducts))
                    {
                        String bilingDate = "";
                        if (StringUtils.isNotEmpty(userId))
                        {
                            IDataset discntDatas = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, EcConstants.ZERO_DISCNT_CODE);
                            if (IDataUtil.isNotEmpty(discntDatas))
                            {// 存在零元资费
                                String endDate = discntDatas.first().getString("END_DATE");
                                if (StringUtils.isNotEmpty(endDate))
                                {
                                    bilingDate = SysDateMgr.getFirstDayOfNextMonth(endDate);
                                }
                            }
                            else
                            {// 不存在零元资费
                                discntDatas = UserDiscntInfoQry.getAllDiscntInfo(userId);
                                if (IDataUtil.isNotEmpty(discntDatas))
                                {
                                    bilingDate = discntDatas.first().getString("START_DATE");
                                }
                            }
                            if (StringUtils.isNotBlank(bilingDate))
                            {
                                bilingDate = SysDateMgr.suffixDate(bilingDate, 0);
                                bilingDate = bilingDate.substring(0, 19);
                            }
                            data.put("BILING_DATE", bilingDate);
                        }
                    }
                }

            }
        }

    }

}
