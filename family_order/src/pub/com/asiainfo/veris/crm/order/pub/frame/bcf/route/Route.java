package com.asiainfo.veris.crm.order.pub.frame.bcf.route;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.org.apache.commons.lang3.StringUtils;

public final class Route
{
    // crm中心库
    public final static String CONN_CRM_CEN = "cen";

    // crm订单库
    public final static String CONN_CRM_JOUR = "jour";

    // OLCOM库
    public final static String CONN_OLCOM = "olcom";

    // crm集团库
    public final static String CONN_CRM_CG = "cg";

    // 系统管理库
    public final static String CONN_SYS = "sys";

    // crm资源库
    public final static String CONN_RES = "res";

    // 终端
    public final static String CONN_TERM = "term";

    // crm日志
    public final static String CONN_LOG = "crm_log";

    // crm营销管理库
    public final static String CONN_MS = "ms";

    // crm历史库
    public final static String CONN_CRM_HIS = "crm_his";

    // 客服中心
    //public final static String CONN_CC = "cc";

    // 统计库
    public final static String CONN_STAT = "sdstat";

    // crm酬金库
    public final static String CONN_REW = "sdrew";

    // 接口
    public final static String CONN_UIF = "uif";

    // 用户归属地州
    public final static String USER_EPARCHY_CODE = "USER_EPARCHY_CODE";

    // 路由地州
    public final static String ROUTE_EPARCHY_CODE = "ROUTE_EPARCHY_CODE";

    // crm库超时限定60秒
    public final static String CONN_CRM_LXF = "crmyt";

    // ESOP库
    public final static String CONN_ESOP = "esop";

    // 统一产品中心连接
    public final static String CONN_UPC = "upc";

    // BUG系统连接
    public final static String CONN_BUG = "bug";

    // UEC历史库
    public final static String CONN_UEC_HIS = "uec_his";

    // crm酬金库
    public final static String CONN_UIF_STAT = "uif_sta";

    // pf库
    public final static String CONN_PF = "pf";

    // UEC库
    public final static String CONN_UOP_UEC = "uec";
    
    // crm产商品库
    public final static String CONN_CRM_UPCCUS = "upccus";

    /**
     * 获取所有crm库名
     * 
     * @return
     * @throws Exception
     */
    public static String[] getAllCrmDb() throws Exception
    {
        String s = BizEnv.getEnvString("crm.allcrmdb", "");

        String[] sz = StringUtils.split(s, ",");
        return sz;
    }

    /**
     * 获取所有jour库名
     * 
     * @return
     * @throws Exception
     */
    public static String[] getAllJourDb() throws Exception
    {

        String s = BizEnv.getEnvString("crm.alljourdb", "");

        if (StringUtils.isNotEmpty(s))
        {
            String[] sz = StringUtils.split(s, ",");
            return sz;
        }
        else
        {
            return getAllCrmDb();
        }

    }

    /**
     * 获得默认crm库名
     * 
     * @return
     * @throws Exception
     */
    public static String getCrmDefaultDb() throws Exception
    {
        String s = BizEnv.getEnvString("crm.default.db");

        return s;
    }

    public static String getJourDb(String routeId) throws Exception
    {
        if (StringUtils.isNotBlank(routeId) && routeId.startsWith(CONN_CRM_JOUR))
        {
            return routeId;
        }
        return CONN_CRM_JOUR + "." + routeId;
    }

//    public static String getJourDb(String routeId) throws Exception
//    {
//        if (StringUtils.isBlank(routeId))
//        {
//            return getJourDbDefault();
//        }
//
//        if (Route.CONN_CRM_CEN.equals(routeId))
//        {
//            return Route.CONN_CRM_CEN;
//        }
//        else
//        {
//            return CONN_CRM_JOUR + "." + routeId;
//        }
//    }

    public static String getJourDbDefault() throws Exception
    {
        return getJourDb(BizRoute.getRouteId());
    }

    public static String getJourDb() throws Exception
    {
        String routeId = BizRoute.getRouteId();

        return getJourDb(routeId);
    }
}
