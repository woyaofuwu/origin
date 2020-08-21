
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RuleCfgInfoQry
{

    public static boolean checkConfigInfo(String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);

        IDataset dataset = Dao.qryByCode("TD_B_QRY_CONFIG", "SEL_CONFIG_INFO", param, Route.CONN_CRM_CEN);
        if (IDataUtil.isEmpty(dataset))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean getRuleConfigInfo(String serviceId, String id, String idType, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        param.put("ID", id);
        param.put("ID_TYPE", idType);
        param.put("EPARCHY_CODE", eparchyCode);

        IDataset dataset = Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_CONFIG_RULE_INFO", param);
        if (IDataUtil.isEmpty(dataset))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 不是品牌的基本服务
     */
    public static boolean hasInfosForNoBrand(String brandCode, String serviceId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("BRAND_CODE", brandCode);
        param.put("SERVICE_ID", serviceId);
        param.put("EPARCHY_CODE", eparchyCode);

        IDataset dataset = Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_FOR_NOBRAND_SERVICE", param);
        if (IDataUtil.isEmpty(dataset))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 不是优惠的基本服务
     */
    public static boolean hasInfosForNoDiscnt(String serviceId, String userId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        param.put("USER_ID", userId);
        param.put("EPARCHY_CODE", eparchyCode);

        IDataset dataset = Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_FOR_NODISCNT_SERVICE", param);
        if (IDataUtil.isEmpty(dataset))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 不是套餐的基本服务
     */
    public static boolean hasInfosForNoPkg(String serviceId, String productId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        param.put("PRODUCT_ID", productId);
        param.put("EPARCHY_CODE", eparchyCode);

        IDataset dataset = Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_FOR_NOPKG_SERVICE", param);
        if (IDataUtil.isEmpty(dataset))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 不是主产品必选服务
     */
    public static boolean hasInfosForNoProduct(String serviceId, String productId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        param.put("PRODUCT_ID", productId);
        param.put("EPARCHY_CODE", eparchyCode);

        IDataset dataset = Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_FOR_NOPRODUCT_SERVICE", param);
        if (IDataUtil.isEmpty(dataset))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 不是不可查询服务
     */
    public static boolean hasInfosForNoQry(String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);

        IDataset dataset = Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_FOR_NOQRY_SERVICE", param);
        if (IDataUtil.isEmpty(dataset))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 不是营销活动内服务
     */
    public static boolean hasInfosForNoSale(String serviceId, String userId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        param.put("USER_ID", userId);
        param.put("EPARCHY_CODE", eparchyCode);

        IDataset dataset = Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_FOR_NOSALE_SERVICE", param);
        if (IDataUtil.isEmpty(dataset))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 取最大的规则集编号
     * 
     * @param scriptId
     * @return
     * @throws Exception
     */
    public static String qryMaxRuleBizRuleId(String tradeTypeCode) throws Exception
    {
        StringBuilder sb = new StringBuilder(20);
        sb.append(SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMMDD));
        if (tradeTypeCode.length() < 4)
        {
            int size = tradeTypeCode.length();
            for (int i = size; i < 4; i++)
            {
                sb.append("0");
            }
        }
        sb.append(tradeTypeCode);
        sb.append("0");

        StringBuilder initId = new StringBuilder(sb);

        StringBuilder sql = new StringBuilder(20);
        sql.append(" select max(to_number(RULE_BIZ_ID))+1 RULE_BIZ_ID from td_bre_business_base  ");
        sql.append(" where RULE_BIZ_ID like '").append(sb.append("__").toString()).append("'");
        IDataset dataset = Dao.qryBySql(sql, null, Route.CONN_CRM_CEN);
        ;
        return dataset.getData(0).getString("RULE_BIZ_ID", initId.append("01").toString());
    }

    /**
     * 取最大的规则编号
     * 
     * @param scriptId
     * @return
     * @throws Exception
     */
    public static String qryMaxRuleId(String tradeTypeCode) throws Exception
    {
        StringBuilder sb = new StringBuilder(20);
        sb.append(SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMMDD));
        if (tradeTypeCode.length() < 4)
        {
            int size = tradeTypeCode.length();
            for (int i = size; i < 4; i++)
            {
                sb.append("0");
            }
        }
        sb.append(tradeTypeCode);
        sb.append("5");

        StringBuilder initId = new StringBuilder(sb);

        StringBuilder sql = new StringBuilder(20);
        sql.append(" select max(to_number(RULE_ID))+1 RULE_ID from td_bre_relation  ");
        sql.append(" where RULE_ID like '").append(sb.append("__").toString()).append("'");
        IDataset dataset = Dao.qryBySql(sql, null, Route.CONN_CRM_CEN);
        ;
        return dataset.getData(0).getString("RULE_ID", initId.append("01").toString());
    }

    /**
     * 查询规则集
     * 
     * @param scriptId
     * @return
     * @throws Exception
     */
    public static IDataset qryRuleBusinessByRuleBizId(String ruleBizId) throws Exception
    {
        IData param = new DataMap();
        param.put("RULE_BIZ_ID", ruleBizId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select TRADE_TYPE_CODE from td_bre_business_base  ");
        parser.addSQL(" where RULE_BIZ_ID = :RULE_BIZ_ID  ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 查询规则
     * 
     * @param scriptId
     * @return
     * @throws Exception
     */
    public static IDataset qryRuleDefinitionByScriptId(String scriptId) throws Exception
    {
        IData param = new DataMap();
        param.put("SCRIPT_ID", scriptId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select script_id,script_type,script_path,SCRIPT_DESCRIPTION from td_bre_definition  ");
        parser.addSQL(" where SCRIPT_ID = :SCRIPT_ID  ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryInfoByServiceId(String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", serviceId);

        return Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_BY_TYPE_ID", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryServiceByType(String erparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", erparchyCode);

        return Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_SERVICE_BY_TYPE", param, Route.CONN_CRM_CEN);
    }
}
