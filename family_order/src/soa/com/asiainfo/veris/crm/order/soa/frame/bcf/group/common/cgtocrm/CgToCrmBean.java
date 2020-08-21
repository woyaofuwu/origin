
package com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.cgtocrm;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class CgToCrmBean
{
    private static void deleteSycnData(IData sycnData) throws Exception
    {
        Dao.delete("TI_B_USER_GRPTOCRM", sycnData, new String[]
        { "SYNC_SEQUENCE", "SYNC_DAY" }, Route.CONN_CRM_CG);
    }

    public static void execCgToCrm(IData data) throws Exception
    {
        // 查询同步数据
        IData sycnData = UserGrpToCrmInfoQry.qrySycnData(data);

        if (IDataUtil.isEmpty(sycnData))
        {
            // 没有查到相应的结果集
            CSAppException.apperr(BizException.CRM_BIZ_5);
            return;
        }

        // 标识：客户、用户、帐户或其他标识
        String ID = sycnData.getString("ID");

        // 标识类型：CT-客户，UR-用户，AC-帐户
        String ID_TYPE = sycnData.getString("ID_TYPE");

        // 用户
        if (ID_TYPE.equals("UR"))
        {
            // 从cg库查用户资料
            IData userData = UcaInfoQry.qryUserInfoByUserId(ID, Route.CONN_CRM_CG);

            if (IDataUtil.isEmpty(userData))
            {
                // 没有查到相应的结果集
                CSAppException.apperr(BizException.CRM_BIZ_5, ID_TYPE, ID);
                return;
            }

            // 遍历更新所有crm库
            String[] routeIds = Route.getAllCrmDb();

            for (int i = 0, len = routeIds.length; i < len; i++)
            {
                syncUserInfo(userData, routeIds[i]);
            }
        }
        else if (ID_TYPE.equals("AC"))
        {
            // 从cg库查账户资料
            IData idsAcct = UcaInfoQry.qryAcctInfoByAcctId(ID, Route.CONN_CRM_CG);

            if (IDataUtil.isEmpty(idsAcct))
            {
                // 没有查到相应的结果集
                CSAppException.apperr(BizException.CRM_BIZ_5, ID_TYPE, ID);
                return;
            }

            IData acctData = idsAcct;

            // 遍历更新所有crm库
            String[] routeIds = Route.getAllCrmDb();

            for (int i = 0, len = routeIds.length; i < len; i++)
            {
                syncAcctInfo(acctData, routeIds[i]);
            }
        }
        else if (ID_TYPE.equals("CM"))
        {
            // 从cg库查集团客户核心资料
            IData customerData = UcaInfoQry.qryCustomerInfoByCustId(ID, Route.CONN_CRM_CG);

            if (IDataUtil.isEmpty(customerData))
            {
                // 没有查到相应的结果集
                CSAppException.apperr(BizException.CRM_BIZ_5, ID_TYPE, ID);
                return;
            }

            // 遍历更新所有crm库
            String[] routeIds = Route.getAllCrmDb();

            for (int i = 0, len = routeIds.length; i < len; i++)
            {
                syncCustomerInfo(customerData, routeIds[i]);

                // delUcaCache
                String cacheKey = "";

                // del cust
                String custId = customerData.getString("CUST_ID");

                cacheKey = CacheKey.getUcaCacheKey(custId, "CustomerByCustId", Route.CONN_CRM_CG);

                SharedCache.delete(cacheKey);
            }
        }
        else if (ID_TYPE.equals("GR"))
        {
            // 从cg库查集团客户资料
            IData idsGroup = UcaInfoQry.qryGrpInfoByCustId(ID);

            if (IDataUtil.isEmpty(idsGroup))
            {
                // 没有查到相应的结果集
                CSAppException.apperr(BizException.CRM_BIZ_5, ID_TYPE, ID);
                return;
            }

            IData groupData = idsGroup;

            // 遍历更新所有crm库
            String[] routeIds = Route.getAllCrmDb();

            for (int i = 0, len = routeIds.length; i < len; i++)
            {
                syncCustgroupInfo(groupData, routeIds[i]);

                // delUcaCache
                String cacheKey = "";

                // del cust
                String custId = groupData.getString("CUST_ID");

                cacheKey = CacheKey.getUcaCacheKey(custId, "GroupByCustId", Route.CONN_CRM_CG);

                SharedCache.delete(cacheKey);

                // del grp
                String groupId = groupData.getString("GROUP_ID");

                cacheKey = CacheKey.getUcaCacheKey(groupId, "GroupByGrpId", Route.CONN_CRM_CG);

                SharedCache.delete(cacheKey);
            }
        }
        else if (ID_TYPE.equals("UP")) // 陕西为了营销活动同步集团主产品信息
        {
        	IData userProductData = UserGrpToCrmInfoQry.qryAllMainProdInfoByUserIdFromDB(ID, Route.CONN_CRM_CG); // 从CG库查用户主产品资料
        	
            if (IDataUtil.isEmpty(userProductData))
            {
                // 没有查到相应的结果集
                CSAppException.apperr(BizException.CRM_BIZ_5, ID_TYPE, ID);
                return;
            }

            // 遍历更新所有CRM库
            String[] routeIds = Route.getAllCrmDb();

            for (int i = 0, len = routeIds.length; i < len; i++)
            {
            	syncUserProductInfo(userProductData, routeIds[i]);
            }
		}
        else if (ID_TYPE.equals("UA")) // 陕西为了营销活动同步集团用户默认付费关系信息
        {
            IData userPayData = UserGrpToCrmInfoQry.qryAllDefaultPayRelaByUserId(ID, Route.CONN_CRM_CG); // 从CG库查用户默认付费关系资料

            if (IDataUtil.isEmpty(userPayData))
            {
                // 没有查到相应的结果集
                CSAppException.apperr(BizException.CRM_BIZ_5, ID_TYPE, ID);
                return;
            }

            // 遍历更新所有CRM库
            String[] routeIds = Route.getAllCrmDb();

            for (int i = 0, len = routeIds.length; i < len; i++)
            {
            	syncUserPayInfo(userPayData, routeIds[i]);
            }
		}

        // 删除同步数据
        deleteSycnData(sycnData);
    }

    // 根据TRADE_ID查找SYNC_SEQUENCE和SYNC_DAY
    public static IDataset execSYNCByTradeId(IData data, Pagination pagination) throws Exception
    {
        IDataset dataset = UserGrpToCrmInfoQry.execSYNCByTradeId(data, pagination);

        return dataset;
    }

    public static IDataset queryInfoByTradeId(IData data, Pagination pagination) throws Exception
    {
        IDataset dataset = UserGrpToCrmInfoQry.queryInfoByTradeId(data, pagination);
        return dataset;
    }

    // 查询TI_B_USER_CRGTOCRM数据库中的TRADE_ID
    public static IDataset queryTradeId(Pagination pagination) throws Exception
    {
        IDataset dataset = UserGrpToCrmInfoQry.getTradeId(pagination);
        return dataset;
    }

    private static boolean syncAcctInfo(IData data, String routeId) throws Exception
    {

        // 先删除
        Dao.delete("TF_F_ACCOUNT", data, new String[]
        { "ACCT_ID", "PARTITION_ID" }, routeId);

        // 后新增
        return Dao.insert("TF_F_ACCOUNT", data, routeId);
    }

    private static boolean syncCustgroupInfo(IData data, String routeId) throws Exception
    {
        // 先删除
        Dao.delete("TF_F_CUST_GROUP", data, new String[]
        { "CUST_ID" }, routeId);

        // 后新增
        return Dao.insert("TF_F_CUST_GROUP", data, routeId);
    }

    private static boolean syncCustomerInfo(IData data, String routeId) throws Exception
    {
        // 先删除
        Dao.delete("TF_F_CUSTOMER", data, new String[]
        { "CUST_ID", "PARTITION_ID" }, routeId);

        // 后新增
        return Dao.insert("TF_F_CUSTOMER", data, routeId);
    }

    private static boolean syncUserInfo(IData data, String routeId) throws Exception
    {
        // 先删除
        Dao.delete("TF_F_USER", data, new String[]
        { "USER_ID", "PARTITION_ID" }, routeId);

        // 后新增
        return Dao.insert("TF_F_USER", data, routeId);
    }
    
    private static boolean syncUserProductInfo(IData data, String routeId) throws Exception
    {
        // 先删除
        Dao.delete("TF_F_USER_PRODUCT", data, new String[]
        { "USER_ID", "PARTITION_ID" }, routeId);

        // 后新增
        return Dao.insert("TF_F_USER_PRODUCT", data, routeId);
    }
    
    private static boolean syncUserPayInfo(IData data, String routeId) throws Exception
    {
        // 先删除
        Dao.delete("TF_A_PAYRELATION", data, new String[]
        { "USER_ID", "PARTITION_ID" }, routeId);

        // 后新增
        return Dao.insert("TF_A_PAYRELATION", data, routeId);
    }
}
