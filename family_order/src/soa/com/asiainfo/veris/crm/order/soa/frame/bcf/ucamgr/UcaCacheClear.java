
package com.asiainfo.veris.crm.order.soa.frame.bcf.ucamgr;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.user.UUserInfoQry;

public class UcaCacheClear
{
    /**
     * 清楚UCA缓存
     * 
     * @param paramValue
     * @param clearType
     * @return
     * @throws Exception
     */
    public static IDataset clear(String paramValue, String clearType) throws Exception
    {
        if (StringUtils.isBlank(paramValue))
        {
            return new DatasetList();
        }

        String sn = "";
        String acctId = "";
        String userId = "";
        String custId = "";
        String groupId = "";

        if (StringUtils.equals("0", clearType))// 服务号码serial_number
        {
            sn = paramValue;
        }
        else if (StringUtils.equals("1", clearType))// 账户编码acct_id
        {
            acctId = paramValue;
        }
        else if (StringUtils.equals("2", clearType))// 用户编码user_id
        {
            userId = paramValue;
        }
        else if (StringUtils.equals("3", clearType))// 客户标识cust_id
        {
            custId = paramValue;
        }
        else if (StringUtils.equals("4", clearType))// 客户编码group_id
        {
            groupId = paramValue;
        }
        else
        {
            return new DatasetList();// 清除类型不对，直接返回空
        }

        IDataset keyList = new DatasetList();

        IDataset areaList = UAreaInfoQry.qryAreaByAreaLevel("20");

        if (IDataUtil.isEmpty(areaList))
        {
            return keyList;
        }

        IData area = new DataMap();

        if (!ProvinceUtil.isProvince(ProvinceUtil.HAIN) && !ProvinceUtil.isProvince(ProvinceUtil.TJIN) && !ProvinceUtil.isProvince(ProvinceUtil.QHAI))
        {
            // 把cg的加进去
            area.put("AREA_CODE", Route.CONN_CRM_CG);
            areaList.add(area);
        }

        // key
        String cacheKey = "";
        String routeId = "";
        IData key = null;

        // get key
        for (int i = 0, isize = areaList.size(); i < isize; i++)
        {
            area = areaList.getData(i);

            routeId = area.getString("AREA_CODE");

            // 过滤不要XX的
            if (routeId.indexOf("XX") != -1)
            {
                continue;
            }

            if (StringUtils.isNotBlank(sn))
            {
                keyList.addAll(clearUcaCacheBySn(sn, routeId));
            }
            else if (StringUtils.isNotBlank(acctId))
            {
                keyList.addAll(clearUcaCacheByAcctId(acctId, routeId));
            }
            else if (StringUtils.isNotBlank(userId))
            {
                keyList.addAll(clearUcaCacheByUserId(userId, routeId));
            }
            else if (StringUtils.isNotBlank(custId))
            {
                keyList.addAll(clearUcaCacheByCustId(custId, routeId));
            }
            else if (StringUtils.isNotBlank(groupId))
            {
                keyList.addAll(clearUcaCacheByGroupId(groupId, routeId));
            }
            else
            {
                return keyList;
            }
        }

        // clear
        for (int i = 0, isize = keyList.size(); i < isize; i++)
        {
            key = keyList.getData(i);

            cacheKey = key.getString("KEY");

            SharedCache.delete(cacheKey);
        }

        return keyList;
    }

    /**
     * 通过ACCT_ID清UCA缓存
     * 
     * @param acctId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IDataset clearUcaCacheByAcctId(String acctId, String routeId) throws Exception
    {
        IDataset keyList = new DatasetList();

        keyList.add(getClearDataByAcctId(acctId, routeId));

        return keyList;
    }

    /**
     * 通过CUST_ID清UCA缓存
     * 
     * @param custId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IDataset clearUcaCacheByCustId(String custId, String routeId) throws Exception
    {
        IDataset keyList = new DatasetList();

        //
        keyList.addAll(getClearDatasetByCustId(custId, routeId));

        // group
        IData groupInfoData = UcaInfoQry.qryGrpInfoByCustId(custId);
        if (IDataUtil.isNotEmpty(groupInfoData))
        {
            String groupId = groupInfoData.getString("GROUP_ID", "");
            keyList.add(getClearDataByGroupId(groupId, routeId));
        }

        //
        IDataset userInfos = UUserInfoQry.qryUserInfoByCustId(custId);
        if (IDataUtil.isEmpty(userInfos))
        {
            return keyList;
        }

        for (int i = 0, size = userInfos.size(); i < size; i++)
        {
            IData userInfo = userInfos.getData(i);
            String sn = userInfo.getString("SERIAL_NUMBER", "");
            String userId = userInfo.getString("USER_ID", "");

            // sn
            keyList.add(getClearDataBySn(sn, routeId));

            // userid
            keyList.addAll(getClearDataSetByUserId(userId, routeId));

            // acct
            IData acctData = UcaInfoQry.qryDefaultPayRelaByUserId(userId, routeId);
            if (IDataUtil.isNotEmpty(acctData))
            {
                String acctId = acctData.getString("ACCT_ID");
                keyList.add(getClearDataByAcctId(acctId, routeId));
            }
        }

        return keyList;
    }

    /**
     * 根据GROUP_ID清UCA缓存
     * 
     * @param groupId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IDataset clearUcaCacheByGroupId(String groupId, String routeId) throws Exception
    {
        IDataset keyList = new DatasetList();

        // groupid
        keyList.add(getClearDataByGroupId(groupId, routeId));

        // 
        IData groupInfoData = UcaInfoQry.qryGrpInfoByGrpId(groupId);
        if (IDataUtil.isEmpty(groupInfoData))
        {
            return keyList;
        }

        // custid group
        String custId = groupInfoData.getString("CUST_ID", "");
        keyList.addAll(getClearDatasetByCustId(custId, routeId));

        IDataset userInfos = UUserInfoQry.qryUserInfoByCustId(custId);
        if (IDataUtil.isEmpty(userInfos))
        {
            return keyList;
        }

        for (int i = 0, size = userInfos.size(); i < size; i++)
        {
            IData userInfo = userInfos.getData(i);
            String userId = userInfo.getString("USER_ID", "");
            String sn = userInfo.getString("SERIAL_NUMBER", "");

            // sn
            keyList.add(getClearDataBySn(sn, routeId));

            // userid
            keyList.addAll(getClearDataSetByUserId(userId, routeId));

            // acct
            IData acctData = UcaInfoQry.qryDefaultPayRelaByUserId(userId, routeId);
            if (IDataUtil.isNotEmpty(acctData))
            {
                String acctId = acctData.getString("ACCT_ID");
                keyList.add(getClearDataByAcctId(acctId, routeId));
            }
        }

        return keyList;
    }

    /**
     * 通过SERIAL_NUMBER清UCA缓存
     * 
     * @param sn
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IDataset clearUcaCacheBySn(String sn, String routeId) throws Exception
    {
        IDataset keyList = new DatasetList();

        // sn
        IData userData = UcaInfoQry.qryUserInfoBySn(sn, routeId);

        if (IDataUtil.isEmpty(userData))
        {
            return new DatasetList();
        }

        // userid
        String userId = userData.getString("USER_ID");

        // custid
        String custId = userData.getString("CUST_ID");

        keyList.add(getClearDataBySn(sn, routeId));
        keyList.addAll(getClearDataSetByUserId(userId, routeId));
        keyList.addAll(getClearDatasetByCustId(custId, routeId));

        // groupid
        IData groupData = UcaInfoQry.qryGrpInfoByCustId(custId);
        if (IDataUtil.isNotEmpty(groupData))
        {
            String groupId = groupData.getString("GROUP_ID");
            keyList.add(getClearDataByGroupId(groupId, routeId));
        }

        // acct
        IData acctData = UcaInfoQry.qryDefaultPayRelaByUserId(userId, routeId);
        if (IDataUtil.isNotEmpty(acctData))
        {
            String acctId = acctData.getString("ACCT_ID");
            keyList.add(getClearDataByAcctId(acctId, routeId));
        }

        return keyList;
    }

    /**
     * 通过USER_ID清UCA缓存
     * 
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IDataset clearUcaCacheByUserId(String userId, String routeId) throws Exception
    {
        IDataset keyList = new DatasetList();

        // userid
        keyList.addAll(getClearDataSetByUserId(userId, routeId));

        // acct
        IData acctData = UcaInfoQry.qryDefaultPayRelaByUserId(userId, routeId);
        if (IDataUtil.isNotEmpty(acctData))
        {
            String acctId = acctData.getString("ACCT_ID");
            keyList.add(getClearDataByAcctId(acctId, routeId));
        }

        // 
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId, routeId);
        if (IDataUtil.isEmpty(userInfo))
        {
            return keyList;
        }

        // sn
        String sn = userInfo.getString("SERIAL_NUMBER", "");
        keyList.add(getClearDataBySn(sn, routeId));

        // custid
        String custId = userInfo.getString("CUST_ID", "");
        keyList.addAll(getClearDatasetByCustId(custId, routeId));

        // groupid
        IData groupInfo = UcaInfoQry.qryGrpInfoByCustId(custId);
        if (IDataUtil.isNotEmpty(groupInfo))
        {
            String groupId = groupInfo.getString("GROUP_ID", "");
            keyList.add(getClearDataByGroupId(groupId, routeId));
        }

        return keyList;
    }

    /**
     * 根据ACCT_ID获取UCA缓存的KEY
     * 
     * @param acctId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IData getClearDataByAcctId(String acctId, String routeId) throws Exception
    {
        // acctid
        String cacheKey = CacheKey.getUcaKeyActByAcctId(acctId, routeId);
        IData key = new DataMap();
        key.put("KEY", cacheKey);

        return key;
    }

    /**
     * 根据GROUP_ID获取UCA缓存的KEY
     * 
     * @param groupId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IData getClearDataByGroupId(String groupId, String routeId) throws Exception
    {
        // grupid group
        String cacheKey = CacheKey.getUcaKeyGroupByGrpId(groupId, routeId);
        IData key = new DataMap();
        key.put("KEY", cacheKey);

        return key;
    }

    /**
     * 根据SN获取UCA缓存的KEY
     * 
     * @param sn
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IData getClearDataBySn(String sn, String routeId) throws Exception
    {
        // user sn
        String cacheKey = CacheKey.getUcaKeyUserBySn(sn, routeId);
        IData key = new DataMap();
        key.put("KEY", cacheKey);

        return key;
    }

    /**
     * 根据CUST_ID获取UCA缓存的KEY
     * 
     * @param custId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IDataset getClearDatasetByCustId(String custId, String routeId) throws Exception
    {
        IDataset keyList = new DatasetList();

        // custid customer
        String cacheKey = CacheKey.getUcaKeyCustomerByCustId(custId, routeId);
        IData key = new DataMap();
        key.put("KEY", cacheKey);
        keyList.add(key);

        // custid person
        cacheKey = CacheKey.getUcaKeyPersonByCustId(custId, routeId);
        key = new DataMap();
        key.put("KEY", cacheKey);
        keyList.add(key);

        // custid group
        cacheKey = CacheKey.getUcaKeyGroupByCustId(custId, routeId);
        key = new DataMap();
        key.put("KEY", cacheKey);
        keyList.add(key);

        return keyList;
    }

    /**
     * 根据USER_ID获取UCA缓存的KEY
     * 
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IDataset getClearDataSetByUserId(String userId, String routeId) throws Exception
    {
        IDataset keyList = new DatasetList();

        // user userid
        String cacheKey = CacheKey.getUcaKeyUserByUserId(userId, routeId);
        IData key = new DataMap();
        key.put("KEY", cacheKey);
        keyList.add(key);

        // user main product
        cacheKey = CacheKey.getUcaKeyMainProdByUserId(userId, routeId);
        key = new DataMap();
        key.put("KEY", cacheKey);
        keyList.add(key);

        // default pay
        cacheKey = CacheKey.getUcaKeyDefaultPayRelaByUserId(userId, routeId);
        key = new DataMap();
        key.put("KEY", cacheKey);
        keyList.add(key);

        // acctDays
        cacheKey = CacheKey.getUcaKeyAcctDaysByUserId(userId, routeId);
        key = new DataMap();
        key.put("KEY", cacheKey);
        keyList.add(key);

        return keyList;
    }
}
