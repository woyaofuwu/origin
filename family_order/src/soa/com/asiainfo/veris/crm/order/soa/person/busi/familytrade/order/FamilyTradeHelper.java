
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class FamilyTradeHelper
{

    public static void addVirtualUser(BusiTradeData bd, String userIdA) throws Exception
    {
        UcaData uca = bd.getRD().getUca();
        String sysdate = bd.getRD().getAcceptTime();

        String custIdA = SeqMgr.getCustId();
        String acctIdA = SeqMgr.getAcctId();
        String virtualSn = "QQ" + uca.getSerialNumber();
        String acctDay = uca.getAcctDay();

        // 新增用户
        UserTradeData user = new UserTradeData();
        user.setUserId(userIdA);
        user.setCustId(custIdA);
        user.setUsecustId(custIdA);
        user.setAcctTag("0");
        user.setEparchyCode(uca.getUserEparchyCode());
        user.setCityCode(uca.getUser().getCityCode());
        user.setUserTypeCode("0");
        user.setSerialNumber(virtualSn);
        user.setPrepayTag("0");
        user.setOpenDate(sysdate);
        user.setOpenMode("0");
        user.setUserStateCodeset("0");
        user.setNetTypeCode("00");
        user.setMputeMonthFee("0");
        user.setInDate(sysdate);
        user.setRemoveTag("0");
        user.setInStaffId(CSBizBean.getVisit().getStaffId());
        user.setInDepartId(CSBizBean.getVisit().getDepartId());
        user.setOpenStaffId(CSBizBean.getVisit().getStaffId());
        user.setOpenDepartId(CSBizBean.getVisit().getDepartId());
        user.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
        user.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
        user.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(virtualSn, user);

        // 新增客户
        CustomerTradeData customer = uca.getCustomer().clone();
        customer.setCustId(custIdA);
        customer.setCustType("0");
        customer.setCustState("0");
        customer.setPsptId("0");
        customer.setRemark("办理亲情业务插入虚拟号码");
        customer.setIsRealName("");
        customer.setSimpleSpell("");
        customer.setCityCodeA("");
        customer.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(virtualSn, customer);

        // 新增帐户
        AccountTradeData acct = new AccountTradeData();
        acct.setEparchyCode(uca.getUserEparchyCode());
        acct.setCityCode(uca.getUser().getCityCode());
        acct.setAcctId(acctIdA);
        acct.setCustId(custIdA);
        acct.setPayName(uca.getCustomer().getCustName());
        acct.setPayModeCode("0");
        acct.setScoreValue("0");
        acct.setOpenDate(sysdate);
        acct.setRemoveTag("0");
        acct.setBasicCreditValue("0");
        acct.setCreditValue("0");
        acct.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(virtualSn, acct);

        // 新增付费关系
        PayRelationTradeData payRela = new PayRelationTradeData();
        payRela.setModifyTag(BofConst.MODIFY_TAG_ADD);
        payRela.setUserId(userIdA);
        payRela.setAcctId(acctIdA);
        payRela.setPayitemCode("0");
        payRela.setAcctPriority("0");
        payRela.setUserPriority("0");
        payRela.setBindType("0");
        payRela.setStartCycleId(SysDateMgr.decodeTimestamp(SysDateMgr.getFirstDayOfThisMonth(), SysDateMgr.PATTERN_TIME_YYYYMMDD));
        payRela.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payRela.setDefaultTag("1");
        payRela.setActTag("1");
        payRela.setLimitType("0");
        payRela.setLimit("0");
        payRela.setComplementTag("0");
        payRela.setInstId(SeqMgr.getInstId());
        bd.add(virtualSn, payRela);

        // 建立虚拟用户与主卡的关系
        RelationTradeData relationUU = new RelationTradeData();
        relationUU.setModifyTag(BofConst.MODIFY_TAG_ADD);
        relationUU.setUserIdA(userIdA);
        relationUU.setUserIdB(uca.getUserId());
        relationUU.setSerialNumberA(virtualSn);
        relationUU.setSerialNumberB(uca.getSerialNumber());
        relationUU.setRelationTypeCode("75");
        relationUU.setRoleCodeA("0");
        relationUU.setRoleCodeB("1");
        relationUU.setOrderno("0");
        relationUU.setShortCode("0");
        relationUU.setStartDate(sysdate);
        relationUU.setEndDate(SysDateMgr.getTheLastTime());
        relationUU.setInstId(SeqMgr.getInstId());
        bd.add(uca.getSerialNumber(), relationUU);

        // 用户账期
        bd.addOpenUserAcctDayData(userIdA, "1");
    }

    /**
     * 校验成员是否有未完工的工单限制
     * 
     * @throws Exception
     */
    public static void checkMemberUnfinishTrade(IData data) throws Exception
    {
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "");
        String userId = data.getString("USER_ID", "");
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String brandCode = data.getString("BRAND_CODE", "");
        String limitAttr = data.getString("LIMIT_ATTR", "0");
        String limitTag = data.getString("LIMIT_TAG", "0");
        String eparchyCode = data.getString("EPARCHY_CODE", "");
        
        StringBuilder sql = new StringBuilder(200);
        sql.append("SELECT /*+ INDEX(T IDX_TF_B_TRADE_USERID) */TRADE_TYPE_CODE  FROM  TF_B_TRADE T WHERE  T.USER_ID = :USER_ID");
        
        IDataset tradeInfos = Dao.qryBySql(sql, data,Route.getJourDb(eparchyCode));
        
        if(IDataUtil.isNotEmpty(tradeInfos))
        {
        	for(int i=0;i<tradeInfos.size();i++)
        	{
        		IData tradeInfo = tradeInfos.getData(i);
        		String limitTradeTypecode = tradeInfo.getString("TRADE_TYPE_CODE");
        		
        		StringBuilder sql1 = new StringBuilder(2000);
        		sql1.append(" SELECT TRADE_TYPE_CODE, LIMIT_TRADE_TYPE_CODE, BRAND_CODE, LIMIT_ATTR, LIMIT_TAG, TO_CHAR(START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        		sql1.append(" TO_CHAR(END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE, EPARCHY_CODE, REMARK, UPDATE_STAFF_ID, UPDATE_DEPART_ID, ");
        		sql1.append(" TO_CHAR(UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME ");
        		sql1.append(" FROM   TD_S_TRADETYPE_LIMIT A ");
        		sql1.append(" WHERE  TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");
        		sql1.append(" AND A.LIMIT_TRADE_TYPE_CODE =:LIMIT_TRADE_TYPE_CODE ");
        		sql1.append(" AND    (BRAND_CODE = :BRAND_CODE OR BRAND_CODE = 'ZZZZ') ");
        		sql1.append(" AND    LIMIT_ATTR = :LIMIT_ATTR ");
        		sql1.append(" AND    LIMIT_TAG = :LIMIT_TAG ");
        		sql1.append(" AND    SYSDATE BETWEEN START_DATE AND END_DATE ");
        		sql1.append(" AND    (EPARCHY_CODE = :EPARCHY_CODE OR EPARCHY_CODE = 'ZZZZ') ");
        		
        		data.put("LIMIT_TRADE_TYPE_CODE", limitTradeTypecode);
        		IDataset tradeLimit = Dao.qryBySql(sql1, data,Route.CONN_CRM_CEN);
        		
        		if (IDataUtil.isNotEmpty(tradeLimit))
                {
                    String limitTradeTypeCode = tradeLimit.getData(0).getString("LIMIT_TRADE_TYPE_CODE", "");
                    IData tradeTypeList = UTradeTypeInfoQry.getTradeType(limitTradeTypeCode, eparchyCode);
                    if (IDataUtil.isNotEmpty(tradeTypeList))
                    {
                        String strTradeType = tradeTypeList.getString("TRADE_TYPE", "");
                        CSAppException.apperr(FamilyException.CRM_FAMILY_742, serialNumber, strTradeType);
                    }
                }
        	}
        }
       // IDataset tradeLimit = TradeInfoQry.getNoTradeJour(tradeTypeCode, userId, brandCode, limitAttr, limitTag, eparchyCode); 
    }

    /**
     * 终止原有UU
     * 
     * @param userIdA
     * @param relationTypeCode
     * @throws Exception
     */
    public static void clearMebRelByUserIdA(BusiTradeData bd, String userIdA, String relationTypeCode) throws Exception
    {
        String sysdate = bd.getRD().getAcceptTime();
        UcaData uca = bd.getRD().getUca();
        IDataset mebList = RelaUUInfoQry.getAllMebByUSERIDA(userIdA, relationTypeCode);
        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData mebData = mebList.getData(i);
            RelationTradeData delMebRelTradeData = new RelationTradeData(mebData);
            delMebRelTradeData.setEndDate(sysdate);
            delMebRelTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(uca.getSerialNumber(), delMebRelTradeData);
        }
    }

    public static void delVirtualUser(BusiTradeData bd, String userIdA) throws Exception
    {
        // 这里用UcaDataFactory会存在潜在问题，因为这些虚拟用户的号码都是一样的，如果有多个USER
        // 则只会构造一个UCA
        // UcaData virtualUca = UcaDataFactory.getUcaByUserId(userIdA);
        String sysdate = bd.getRD().getAcceptTime();
        UcaData uca = bd.getRD().getUca();

        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userIdA);

        if (IDataUtil.isEmpty(userInfo))
        {
            return;
        }
        if (!"0".equals(userInfo.getString("REMOVE_TAG")))
        {
            // 只处理REMOVE_TAG=0的
            return;
        }

        String serialNumber = userInfo.getString("SERIAL_NUMBER");

        // 终止用户资料
        UserTradeData delUser = new UserTradeData(userInfo);
        delUser.setModifyTag(BofConst.MODIFY_TAG_DEL);
        delUser.setDestroyTime(sysdate);
        delUser.setRemoveTag("1");
        bd.add(serialNumber, delUser);

        // 终止客户资料
        String custId = userInfo.getString("CUST_ID");
        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (IDataUtil.isNotEmpty(custInfo))
        {
            CustomerTradeData delCustomer = new CustomerTradeData(custInfo);
            delCustomer.setModifyTag(BofConst.MODIFY_TAG_DEL);
            delCustomer.setRemoveTag("1");
            delCustomer.setRemoveDate(sysdate);
            bd.add(serialNumber, delCustomer);
        }

        // 终止帐户信息
        IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userIdA);
        if (IDataUtil.isNotEmpty(acctInfo))
        {
            AccountTradeData delAcct = new AccountTradeData(acctInfo);
            delAcct.setModifyTag(BofConst.MODIFY_TAG_DEL);
            delAcct.setRemoveTag("1");
            delAcct.setRemoveDate(sysdate);
            bd.add(serialNumber, delAcct);
        }

        // 终止付费关系
        IData payRela = UcaInfoQry.qryDefaultPayRelaByUserId(userIdA);
        if (IDataUtil.isNotEmpty(payRela))
        {
            PayRelationTradeData delPayRela = new PayRelationTradeData(payRela);
            // UserFamilyMgr.fjgetFirstDayThisAcct
            // 这里获取账期还要看下原CPP方法的逻辑
            String acctDay = uca.getAcctDay();
            delPayRela.setEndCycleId(SysDateMgr.decodeTimestamp(SysDateMgr.getFirstDayOfThisMonth(), SysDateMgr.PATTERN_TIME_YYYYMMDD));
            delPayRela.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(serialNumber, delPayRela);
        }

        // 终止用户主产品信息
        IData userMainPrd = UcaInfoQry.qryMainProdInfoByUserId(userIdA);
        if (IDataUtil.isNotEmpty(userMainPrd))
        {
            ProductTradeData delPrdTD = new ProductTradeData(userMainPrd);
            delPrdTD.setEndDate(sysdate);
            delPrdTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(serialNumber, delPrdTD);
        }

        // 终止用户服务
        IDataset userSvcList = BofQuery.queryUserAllSvc(userIdA, userInfo.getString("EPARCHY_CODE"));
        for (int i = 0, size = userSvcList.size(); i < size; i++)
        {
            IData userSvc = userSvcList.getData(i);
            SvcTradeData delSvcTD = new SvcTradeData(userSvc);
            delSvcTD.setEndDate(sysdate);
            delSvcTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(serialNumber, delSvcTD);
        }
    }

    public static String getDiscntPackageId(String productId, String discntCode) throws Exception
    {
        String packageId = null;
        IDataset tmpList = DiscntInfoQry.getDiscntByProduct(productId);
        for (int i = 0, size = tmpList.size(); i < size; i++)
        {
            IData tmp = tmpList.getData(i);
            String tmpDiscntCode = tmp.getString("DISCNT_CODE");
            if (tmpDiscntCode.equals(discntCode))
            {
                packageId = tmp.getString("PACKAGE_ID");
                break;
            }
        }

        if (StringUtils.isBlank(packageId))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_743, discntCode);
        }

        return packageId;
    }
}
