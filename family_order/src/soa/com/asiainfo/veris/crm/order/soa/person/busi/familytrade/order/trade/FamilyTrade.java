
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.FamilyTradeHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyTradeReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.MemberData;

public class FamilyTrade extends BaseTrade implements ITrade
{
    /**
     * 新增虚拟用户
     * 
     * @param bd
     * @throws Exception
     */
    private void addVirtualUser(BusiTradeData bd, String userIdA) throws Exception
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
        CustomerTradeData customer = new CustomerTradeData();
        CustomerTradeData tempCust = uca.getCustomer();
        customer.setCustId(custIdA);
        customer.setCustName(tempCust.getCustName());
        customer.setCustType("0");
        customer.setCustState("0");
        customer.setPsptId("0");
        customer.setPsptTypeCode(tempCust.getPsptTypeCode());
        customer.setOpenLimit(tempCust.getOpenLimit());
        customer.setEparchyCode(tempCust.getEparchyCode());
        customer.setCityCode(tempCust.getCityCode());
        customer.setCustPasswd(tempCust.getCustPasswd());
        customer.setDevelopDepartId(tempCust.getDevelopDepartId());
        customer.setDevelopStaffId(tempCust.getDevelopStaffId());
        customer.setInDepartId(tempCust.getInDepartId());
        customer.setInStaffId(tempCust.getInStaffId());
        customer.setInDate(tempCust.getInDate());
        customer.setRemoveTag("0");
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

        // 建立用户产品信息
        ProductTradeData addProductTD = new ProductTradeData();
        addProductTD.setUserId(userIdA);
        addProductTD.setProductId("8030");
        addProductTD.setBrandCode("1399");
        addProductTD.setStartDate(sysdate);
        addProductTD.setEndDate(SysDateMgr.getTheLastTime());
        addProductTD.setMainTag("1");
        addProductTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        addProductTD.setProductMode("03");
        addProductTD.setUserIdA("-1");
        addProductTD.setCampnId("0");
        addProductTD.setInstId(SeqMgr.getInstId());
        bd.add(virtualSn, addProductTD);

        // 用户账期
        bd.addOpenUserAcctDayData(userIdA, acctDay);
        bd.addOpenAccountAcctDayData(acctIdA, acctDay);

        // 临时办法
        UcaData virtualUca = DataBusManager.getDataBus().getUca(virtualSn);
        virtualUca.setAcctDay(acctDay);
        virtualUca.setFirstDate(sysdate);
    }

    /**
     * 终止原有UU
     * 
     * @param userIdA
     * @param relationTypeCode
     * @throws Exception
     */
    private void clearMebRelByUserIdA(BusiTradeData bd, String userIdA, String relationTypeCode) throws Exception
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

    /**
     * 清理虚拟用户资料
     * 
     * @param bd
     * @param userIdA
     * @throws Exception
     */
    // private void clearVirtualInfo(BusiTradeData bd, String userIdA) throws Exception{
    // //这里用UcaDataFactory会存在潜在问题，因为这些虚拟用户的号码都是一样的，如果有多个USER
    // //则只会构造一个UCA
    // //UcaData virtualUca = UcaDataFactory.getUcaByUserId(userIdA);
    // String sysdate = bd.getRD().getAcceptTime();
    // UcaData uca = bd.getRD().getUca();
    //		
    // IData userInfo = UcaInfoQry.qryUserInfoByUserId(userIdA);
    // if(IDataUtil.isEmpty(userInfo))
    // {
    // return;
    // }
    // if(!"0".equals(userInfo.getString("REMOVE_TAG")))
    // {
    // //只处理REMOVE_TAG=0的
    // return;
    // }
    //		
    // String serialNumber = userInfo.getString("SERIAL_NUMBER");
    //		
    // //终止用户资料
    // UserTradeData delUser = new UserTradeData(userInfo);
    // delUser.setModifyTag(BofConst.MODIFY_TAG_DEL);
    // delUser.setDestroyTime(sysdate);
    // delUser.setRemoveTag("2");
    // bd.add(serialNumber, delUser);
    //		
    // //终止客户资料
    // String custId = userInfo.getString("CUST_ID");
    // IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
    // if(IDataUtil.isNotEmpty(custInfo))
    // {
    // CustomerTradeData delCustomer = new CustomerTradeData(custInfo);
    // delCustomer.setModifyTag(BofConst.MODIFY_TAG_DEL);
    // delCustomer.setRemoveTag("1");
    // delCustomer.setRemoveDate(sysdate);
    // bd.add(serialNumber, delCustomer);
    // }
    //		
    // //终止帐户信息
    // IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userIdA);
    // if(IDataUtil.isNotEmpty(acctInfo))
    // {
    // AccountTradeData delAcct = new AccountTradeData(acctInfo);
    // delAcct.setModifyTag(BofConst.MODIFY_TAG_DEL);
    // delAcct.setRemoveTag("1");
    // delAcct.setRemoveDate(sysdate);
    // bd.add(serialNumber, delAcct);
    // }
    //		
    // //终止付费关系
    // IData payRela = UcaInfoQry.qryDefaultPayRelaByUserId(userIdA);
    // if(IDataUtil.isNotEmpty(payRela))
    // {
    // PayRelationTradeData delPayRela = new PayRelationTradeData(payRela);
    // //UserFamilyMgr.fjgetFirstDayThisAcct
    // //这里获取账期还要看下原CPP方法的逻辑
    // String acctDay = uca.getAcctDay();
    // delPayRela.setEndCycleId(SysDateMgr.decodeTimestamp(SysDateMgr.getFirstDayOfThisMonth(),
    // SysDateMgr.PATTERN_TIME_YYYYMMDD));
    // delPayRela.setModifyTag(BofConst.MODIFY_TAG_DEL);
    // bd.add(serialNumber, delPayRela);
    // }
    // }

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        FamilyTradeReqData reqData = (FamilyTradeReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String sysdate = reqData.getAcceptTime();
        String userId = uca.getUserId();
        boolean addFamilyFlag = false;

        String discntCode = getDiscntCodeByBrandCode(uca.getBrandCode(), uca.getProductId());
        List<DiscntTradeData> userDiscntList = uca.getUserDiscntByDiscntId(discntCode);
        if (userDiscntList == null || userDiscntList.size() == 0)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_43);
        }
        // 如果用户存在该优惠且user_id_a为-1，则需要删除这部分数据
        // 暂只考虑用户有且只有一条该优惠，如果涉及多条该逻辑需改动
        DiscntTradeData userDiscnt = userDiscntList.get(0);
        String userIdA = userDiscnt.getUserIdA();
        if ("-1".equals(userIdA))
        {
            addFamilyFlag = true;// 表示本次需新增亲情虚拟用户

            // 原来的逻辑是新增一条优惠，然后删一条优惠，本次改造成修改原有优惠
            userIdA = updMainUserDiscnt(bd, userDiscnt);
            // DiscntTradeData delUserDiscnt = userDiscnt.clone();
            // delUserDiscnt.setModifyTag(BofConst.MODIFY_TAG_DEL);
            // delUserDiscnt.setEndDate(sysdate);
            // bd.add(uca.getSerialNumber(), delUserDiscnt);

            // 还要终止原来该用户的垃圾UU关系数据，
            IDataset relationUUList = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("75", userId, "1");
            for (int i = 0, size = relationUUList.size(); i < size; i++)
            {
                IData relationUU = relationUUList.getData(i);
                String oldUserIdA = relationUU.getString("USER_ID_A");
                String relationTypeCode = relationUU.getString("RELATION_TYPE_CODE");
                // 老代码还插了一条MODIFY_TAG=9的TF_B_TRADE_RELATION数据，暂时先不写，看后续这个是有什么用
                // ssUserFamilyMgr.ClearOldFamilyUser

                // 终止原有UU
                clearMebRelByUserIdA(bd, oldUserIdA, relationTypeCode);
                // 清理虚拟用户资料
                // clearVirtualInfo(bd, oldUserIdA);改成用FamilyTradeHelper.delVirtualUser
                FamilyTradeHelper.delVirtualUser(bd, oldUserIdA);
            }
        }

        if (addFamilyFlag)
        {
            // 新增虚拟用户
            addVirtualUser(bd, userIdA);
        }

        boolean delVirtualUserFlag = dealDeckFamilyUser(bd, userIdA);

        if (delVirtualUserFlag)
        {
            delVirtualUser(bd, userIdA, userDiscnt);
        }
    }

    /**
     * 处理副卡
     * 
     * @param bd
     * @throws Exception
     */
    private boolean dealDeckFamilyUser(BusiTradeData bd, String userIdA) throws Exception
    {
        FamilyTradeReqData reqData = (FamilyTradeReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String sysdate = reqData.getAcceptTime();
        List<MemberData> mebDataList = reqData.getMemberDataList();
        String virtualSn = "QQ" + uca.getSerialNumber();
        boolean delVirtualUserFlag = false;// 删除亲情虚拟用户
        boolean updFamilyTimes = false;

        IDataset mebList = RelaUUInfoQry.getUserRelationAll(userIdA, "75");
        int currMebCount = 0;
        if (mebList.size() > 0)
        {
            currMebCount = mebList.size() - 1;// -1是因为这个SQL的结果包含了主卡的，这个主要用于判断是否亲情下副卡全部删除了
        }

        for (int i = 0, size = mebDataList.size(); i < size; i++)
        {
            MemberData mebData = mebDataList.get(i);
            String modifyTag = mebData.getModifyTag();
            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                UcaData deckUca = mebData.getUca();
                RelationTradeData addRela = new RelationTradeData();
                addRela.setModifyTag(BofConst.MODIFY_TAG_ADD);
                addRela.setUserIdA(userIdA);
                addRela.setUserIdB(deckUca.getUserId());
                addRela.setSerialNumberA(virtualSn);
                addRela.setSerialNumberB(deckUca.getSerialNumber());
                addRela.setRelationTypeCode("75");
                addRela.setRoleCodeA("0");
                addRela.setRoleCodeB("2");// 副卡
                addRela.setOrderno(mebData.getOrderNo());
                addRela.setStartDate(sysdate);
                addRela.setEndDate(SysDateMgr.getTheLastTime());
                addRela.setInstId(SeqMgr.getInstId());
                bd.add(uca.getSerialNumber(), addRela);

                currMebCount++;
            }
            else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                updFamilyTimes = true;

                String instId = mebData.getInstId();
                IData relaData = RelaUUInfoQry.getRelationByInstId(instId);
                if (IDataUtil.isEmpty(relaData))
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_44, instId);
                }
                RelationTradeData delRela = new RelationTradeData(relaData);
                delRela.setModifyTag(BofConst.MODIFY_TAG_DEL);
                delRela.setEndDate(sysdate);
                bd.add(uca.getSerialNumber(), delRela);

                currMebCount--;
            }
            else
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_12);
            }
        }

        // 如果是全部删除，则删除other表数据
        if (currMebCount == 0)
        {
            delVirtualUserFlag = true;
        }
        else
        {
            // 修改次数+1
            if (updFamilyTimes)
            {
                IData otherInfo = new DataMap();
                IDataset result = UserOtherInfoQry.getOtherInfoByCodeUserId(uca.getUserId(), "1399");
                if (IDataUtil.isNotEmpty(result))
                {
                    for (int i = 0, size = result.size(); i < size; i++)
                    {
                        IData tmp = result.getData(i);
                        String rsrvValue = tmp.getString("RSRV_VALUE", "");
                        String rsrvStr1 = tmp.getString("RSRV_STR1", "");
                        if (uca.getProductId().equals(rsrvValue) && "HAIN".equals(rsrvStr1))
                        {
                            otherInfo = tmp;
                            break;
                        }
                    }
                }

                if (IDataUtil.isEmpty(otherInfo))
                {
                    // 新增一条OTHER信息
                    OtherTradeData otherTD = new OtherTradeData();
                    otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    otherTD.setRsrvValueCode("1399");
                    otherTD.setUserId(uca.getUserId());
                    otherTD.setRsrvValue(uca.getProductId());
                    otherTD.setRsrvStr1("HAIN");
                    otherTD.setRsrvStr2("1");
                    otherTD.setStartDate(sysdate);
                    otherTD.setEndDate(SysDateMgr.getDateLastMonthSec(sysdate));
                    otherTD.setInstId(SeqMgr.getInstId());
                    bd.add(uca.getSerialNumber(), otherTD);
                }
                else
                {
                    OtherTradeData otherTD = new OtherTradeData(otherInfo);
                    otherTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
                    otherTD.setRsrvStr2(String.valueOf(otherInfo.getInt("RSRV_STR2") + 1));
                    bd.add(uca.getSerialNumber(), otherTD);
                }
            }
        }

        return delVirtualUserFlag;
    }

    /**
     * 删除亲情虚拟用户
     * 
     * @param bd
     * @param userIdA
     * @throws Exception
     */
    private void delVirtualUser(BusiTradeData bd, String userIdA, DiscntTradeData userDiscnt) throws Exception
    {
        UcaData uca = bd.getRD().getUca();
        String sysdate = bd.getRD().getAcceptTime();

        // 修改主卡用户的亲情优惠
        DiscntTradeData updUserDiscntTD = userDiscnt.clone();
        updUserDiscntTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
        updUserDiscntTD.setUserIdA("-1");
        updUserDiscntTD.setSpecTag("0");
        updUserDiscntTD.setRelationTypeCode("");
        bd.add(uca.getSerialNumber(), updUserDiscntTD);

        // 终止虚拟用户与主卡的对应关系
        IData mainRela = RelaUUInfoQry.getRelaByPK(userIdA, uca.getUserId(), "75");
        if (IDataUtil.isEmpty(mainRela))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_45);
        }
        RelationTradeData relaTD = new RelationTradeData(mainRela);
        relaTD.setEndDate(sysdate);
        relaTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
        bd.add(uca.getSerialNumber(), relaTD);

        // 删除user_other add by zhouwu 2014-06-06
        IData otherInfo = new DataMap();
        IDataset result = UserOtherInfoQry.getOtherInfoByCodeUserId(uca.getUserId(), "1399");
        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0, size = result.size(); i < size; i++)
            {
                IData tmp = result.getData(i);
                String rsrvValue = tmp.getString("RSRV_VALUE", "");
                String rsrvStr1 = tmp.getString("RSRV_STR1", "");
                if (uca.getProductId().equals(rsrvValue) && "HAIN".equals(rsrvStr1))
                {
                    otherInfo = tmp;
                    break;
                }
            }
        }
        if (IDataUtil.isNotEmpty(otherInfo))
        {
            OtherTradeData otherTD = new OtherTradeData(otherInfo);
            otherTD.setEndDate(sysdate);
            otherTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(uca.getSerialNumber(), otherTD);
        }

        // clearVirtualInfo(bd, userIdA);改成用FamilyTradeHelper.delVirtualUser
        FamilyTradeHelper.delVirtualUser(bd, userIdA);

    }

    /**
     * 清理虚拟用户资料
     * 
     * @param bd
     * @param userIdA
     * @throws Exception
     */
    // private void clearVirtualInfo(BusiTradeData bd, String userIdA) throws Exception{
    // //这里用UcaDataFactory会存在潜在问题，因为这些虚拟用户的号码都是一样的，如果有多个USER
    // //则只会构造一个UCA
    // //UcaData virtualUca = UcaDataFactory.getUcaByUserId(userIdA);
    // String sysdate = bd.getRD().getAcceptTime();
    // UcaData uca = bd.getRD().getUca();
    //		
    // IData userInfo = UcaInfoQry.qryUserInfoByUserId(userIdA);
    // if(IDataUtil.isEmpty(userInfo))
    // {
    // return;
    // }
    // if(!"0".equals(userInfo.getString("REMOVE_TAG")))
    // {
    // //只处理REMOVE_TAG=0的
    // return;
    // }
    //		
    // String serialNumber = userInfo.getString("SERIAL_NUMBER");
    //		
    // //终止用户资料
    // UserTradeData delUser = new UserTradeData(userInfo);
    // delUser.setModifyTag(BofConst.MODIFY_TAG_DEL);
    // delUser.setDestroyTime(sysdate);
    // delUser.setRemoveTag("2");
    // bd.add(serialNumber, delUser);
    //		
    // //终止客户资料
    // String custId = userInfo.getString("CUST_ID");
    // IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
    // if(IDataUtil.isNotEmpty(custInfo))
    // {
    // CustomerTradeData delCustomer = new CustomerTradeData(custInfo);
    // delCustomer.setModifyTag(BofConst.MODIFY_TAG_DEL);
    // delCustomer.setRemoveTag("1");
    // delCustomer.setRemoveDate(sysdate);
    // bd.add(serialNumber, delCustomer);
    // }
    //		
    // //终止帐户信息
    // IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userIdA);
    // if(IDataUtil.isNotEmpty(acctInfo))
    // {
    // AccountTradeData delAcct = new AccountTradeData(acctInfo);
    // delAcct.setModifyTag(BofConst.MODIFY_TAG_DEL);
    // delAcct.setRemoveTag("1");
    // delAcct.setRemoveDate(sysdate);
    // bd.add(serialNumber, delAcct);
    // }
    //		
    // //终止付费关系
    // IData payRela = UcaInfoQry.qryDefaultPayRelaByUserId(userIdA);
    // if(IDataUtil.isNotEmpty(payRela))
    // {
    // PayRelationTradeData delPayRela = new PayRelationTradeData(payRela);
    // //UserFamilyMgr.fjgetFirstDayThisAcct
    // //这里获取账期还要看下原CPP方法的逻辑
    // String acctDay = uca.getAcctDay();
    // delPayRela.setEndCycleId(SysDateMgr.decodeTimestamp(SysDateMgr.getFirstDayOfThisMonth(),
    // SysDateMgr.PATTERN_TIME_YYYYMMDD));
    // delPayRela.setModifyTag(BofConst.MODIFY_TAG_DEL);
    // bd.add(serialNumber, delPayRela);
    // }
    // }

    private String getDiscntCodeByBrandCode(String brandCode, String productId) throws Exception
    {
        String discntCode = null;
        if ("G010".equals(brandCode))
        {// 动感地带
            discntCode = "866";
        }
        else if ("G002".equals(brandCode) && "10001254".equals(productId))
        {// 神州行轻松卡产品
            discntCode = "1255";
        }
        else if ("G002".equals(brandCode) && "10001264".equals(productId))
        {// 神州行幸福卡产品
            discntCode = "1266";
        }
        else
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_42);
        }
        return discntCode;
    }

    // 给主卡修改优惠信息
    private String updMainUserDiscnt(BusiTradeData bd, DiscntTradeData userDiscnt) throws Exception
    {
        UcaData uca = bd.getRD().getUca();
        String sysdate = bd.getRD().getAcceptTime();
        String userIdA = SeqMgr.getUserId();
        // 给主卡绑优惠
        DiscntTradeData mainUserDiscnt = userDiscnt.clone();
        mainUserDiscnt.setUserIdA(userIdA);
        mainUserDiscnt.setSpecTag("2");
        mainUserDiscnt.setRelationTypeCode("75");
        mainUserDiscnt.setModifyTag(BofConst.MODIFY_TAG_UPD);
        bd.add(uca.getSerialNumber(), mainUserDiscnt);

        return userIdA;
    }

}
