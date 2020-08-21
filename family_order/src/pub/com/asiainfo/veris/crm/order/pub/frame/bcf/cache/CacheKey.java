
package com.asiainfo.veris.crm.order.pub.frame.bcf.cache;

import com.ailk.biz.service.BizRoute;
import com.ailk.database.config.DBRouteCfg;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;

public final class CacheKey
{
    private final static String BCC_CRM_CUST_TOUCH = "BCC_CRM_CUST_TOUCH_";// 客户接触

    private final static String BCC_CRM_USER_PWD = "BCC_CRM_USER_PWD_";// 客户密码锁

    private final static String BCC_CRM_LOCK_TRADE = "BCC_CRM_LOCK_TRADE_";// 业务登记锁

    private final static String SHC_CRM_BBOSS = "SHC_CRM_BBOSS_";// BBOSS资料缓存

    private final static String SHC_CRM_TRADE = "SHC_CRM_TRADE_";// 订单缓存
    
    private final static String SHC_CRM_CONTACT = "SHC_CRM_CONTACT_";// 合同缓存

    private final static String SHC_CRM_UCA = "SHC_CRM_UCA_";// 3户资料缓存

    private final static String SHC_CRM_IVR = "SHC_CRM_IVR_";// ivr接入

    private final static String SHC_CRM_SELECT_ELEMENT = "SHC_CRM_SELECT_ELEMENT";// 用户选择元素缓存
    
    private final static String SHC_CRM_DATALINE = "SHC_CRM_DATALINE_";//专线资料缓存
    
    private final static String SHC_CRM_TERMINAL = "SHC_CRM_TERMINAL_";//专线资料缓存

    public static String getBBossESOPInfoKey(String staffId, String groupId) throws Exception
    {
        StringBuilder cacheKey = new StringBuilder(80);

        // 客户、用户(userid，sn)、账户
        cacheKey.append(SHC_CRM_BBOSS).append("BBOSS_ESOP_INFO_").append(staffId).append("_").append(groupId);

        return cacheKey.toString();
    }

    public static String getBossInitInfoKey(String staffId, String groupId) throws Exception
    {
        StringBuilder cacheKey = new StringBuilder(80);

        // 客户、用户(userid，sn)、账户
        cacheKey.append(SHC_CRM_BBOSS).append("BBOSS_INIT_INFO_").append(staffId).append("_").append(groupId);

        return cacheKey.toString();
    }
    public static String getContactInfoKey(String custID) throws Exception
    {
        StringBuilder cacheKey = new StringBuilder(80);

        // 客户、用户(userid，sn)、账户
        cacheKey.append(SHC_CRM_CONTACT).append("CONTACT_INFO_").append(custID);

        return cacheKey.toString();
    }
    
     public static String getBossBatchInfoKey(String batchId) throws Exception
    {
        StringBuilder cacheKey = new StringBuilder(80);

        // 客户、用户(userid，sn)、账户
        cacheKey.append(SHC_CRM_BBOSS).append("BBOSS_BATCH_INFO_").append(batchId);

        return cacheKey.toString();
    }

    public static String getBossProductInfoKey(String staffId, String groupId) throws Exception
    {
        StringBuilder cacheKey = new StringBuilder(80);

        // 客户、用户(userid，sn)、账户
        cacheKey.append(SHC_CRM_BBOSS).append("BBOSS_PRODUCT_INFO_").append(staffId).append("_").append(groupId);

        return cacheKey.toString();
    }

    public static String getCreditTradeCtrl(String eparchyCode) throws Exception
    {
        StringBuilder cacheKey = new StringBuilder(80);

        cacheKey.append(SHC_CRM_TRADE).append("CREDIT_TRADE_CONTROL_").append(eparchyCode);

        return cacheKey.toString();
    }

    public static String getCustTouchKey(String sn) throws Exception
    {

        StringBuilder cacheKey = new StringBuilder(80);

        // 客户、用户(userid，sn)、账户
        cacheKey.append(BCC_CRM_CUST_TOUCH).append(sn);

        return cacheKey.toString();
    }

    public static String getIvrKey(String staffId) throws Exception
    {
        StringBuilder cacheKey = new StringBuilder(80);

        cacheKey.append(SHC_CRM_IVR).append(staffId);

        return cacheKey.toString();
    }

    public static String getSelectElemtInfoKey(String id, String productId, String tradeTypeCode) throws Exception
    {
        StringBuilder cacheKey = new StringBuilder(80);

        // 客户、用户(userid，sn)、账户
        cacheKey.append(SHC_CRM_SELECT_ELEMENT).append(id).append("_").append(productId).append("_").append(tradeTypeCode);

        return cacheKey.toString();
    }

    public static String getTradeLockKey(String lockObj) throws Exception
    {
        StringBuilder cacheKey = new StringBuilder(80);

        // 客户、用户(userid，sn)、账户
        cacheKey.append(BCC_CRM_LOCK_TRADE).append(lockObj);

        return cacheKey.toString();
    }

    public static String getUcaCacheKey(String idValue, String idType, String routeId) throws Exception
    {
        if (StringUtils.isBlank(routeId))
        {
            routeId = BizRoute.getRouteId();
        }

        StringBuilder cacheKey = new StringBuilder(80);

        // 客户、用户(userid，sn)、账户
        String dbString = DBRouteCfg.getRoute("crm", routeId);
        cacheKey.append(SHC_CRM_UCA).append(SysDateMgr.getSysDate("dd")).append("_").append(dbString).append("_").append(idType).append("_").append(idValue);

        return cacheKey.toString();
    }

    public static String getUcaKeyAcctDaysByUserId(String userId, String routeId) throws Exception
    {
        String cacheKey = getUcaCacheKey(userId, "AcctDaysByUserId", routeId);

        return cacheKey;
    }

    public static String getUcaKeyActByAcctId(String acctId, String routeId) throws Exception
    {

        String cacheKey = getUcaCacheKey(acctId, "ActByAcctId", routeId);

        return cacheKey;
    }

    public static String getUcaKeyCustomerByCustId(String custId, String routeId) throws Exception
    {

        String cacheKey = getUcaCacheKey(custId, "CustomerByCustId", routeId);

        return cacheKey;
    }

    public static String getUcaKeyDefaultPayRelaByUserId(String userId, String routeId) throws Exception
    {
        String cacheKey = getUcaCacheKey(userId, "DefaultPayRelaByUserId", routeId);

        return cacheKey;
    }

    public static String getUcaKeyGroupByCustId(String custId, String routeId) throws Exception
    {
        String cacheKey = getUcaCacheKey(custId, "GroupByCustId", routeId);

        return cacheKey;
    }

    public static String getUcaKeyGroupByGrpId(String groupId, String routeId) throws Exception
    {

        String cacheKey = getUcaCacheKey(groupId, "GroupByGrpId", routeId);

        return cacheKey;
    }

    public static String getUcaKeyMainProdByUserId(String userId, String routeId) throws Exception
    {

        String cacheKey = getUcaCacheKey(userId, "MainProdByUserId", routeId);

        return cacheKey;
    }

    public static String getUcaKeyPersonByCustId(String custId, String routeId) throws Exception
    {

        String cacheKey = getUcaCacheKey(custId, "PersonByCustId", routeId);

        return cacheKey;
    }

    public static String getUcaKeyUserBySn(String sn, String routeId) throws Exception
    {

        String cacheKey = getUcaCacheKey(sn, "UserBySn", routeId);

        return cacheKey;
    }

    public static String getUcaKeyUserByUserId(String userId, String routeId) throws Exception
    {

        String cacheKey = getUcaCacheKey(userId, "UserByUserId", routeId);

        return cacheKey;
    }

    public static String getUserPwdLockKey(String userId, String routeId) throws Exception
    {

        StringBuilder cacheKey = new StringBuilder(80);
        String dbString = DBRouteCfg.getRoute("crm", routeId);
        cacheKey.append(BCC_CRM_USER_PWD).append(userId).append("_").append(dbString);

        return cacheKey.toString();
    }
    
    public static String getUcaKeyVipByUserId(String userId, String routeId) throws Exception
    {

        String cacheKey = getUcaCacheKey(userId, "VipByUserId", routeId);

        return cacheKey;
    }
    
    public static String getUcaKeyVipBySn(String sn, String routeId) throws Exception
    {

        String cacheKey = getUcaCacheKey(sn, "VipBySn", routeId);

        return cacheKey;
    }
    
    public static String getUcaKeyDataLine(String ibsysid,String recordNum) throws Exception
    {
    	StringBuilder cacheKey = new StringBuilder(80);

        // 订单号 加专线标识
        cacheKey.append(SHC_CRM_DATALINE).append(ibsysid).append("_").append(recordNum);

        return cacheKey.toString();
    }

	/**
	 * @description
	 * @param @param resCode
	 * @param @return
	 * @return String
	 * @author tanzheng
	 * @date 2019年5月14日
	 * @param resCode
	 * @return
	 */
	public static String getUsedTmByIMEI(String resCode) {
		// TODO Auto-generated method stub
		return SHC_CRM_TERMINAL+resCode;
	}
}
