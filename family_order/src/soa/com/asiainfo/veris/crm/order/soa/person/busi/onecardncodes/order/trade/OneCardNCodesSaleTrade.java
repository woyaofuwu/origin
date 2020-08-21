/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order.trade;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order.requestdata.OneCardNCodesChangeCardReqData;

public class OneCardNCodesSaleTrade extends BaseTrade implements ITrade
{

    /**
     * @Function: createAcctTradeData()
     * @Description: 账户台帐
     * @param:
     * @return：
     */
    public void createAcctTradeData(BusiTradeData bd, IData userinfo, IData custinfo, IData acctinfo) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) bd.getRD();
        AccountTradeData accout = new AccountTradeData();
        accout.setAcctId(acctinfo.getString("ACCT_ID", ""));
        accout.setCustId(userinfo.getString("CUST_ID", ""));
        accout.setPayName(acctinfo.getString("PAY_NAME", ""));
        accout.setPayModeCode(acctinfo.getString("PAY_MODE_CODE", ""));
        accout.setModifyTag(BofConst.MODIFY_TAG_ADD);
        accout.setScoreValue(reqData.getUca().getAccount().getScoreValue());
        accout.setBasicCreditValue(reqData.getUca().getAccount().getBasicCreditValue());
        accout.setCreditValue(reqData.getUca().getAccount().getCreditValue());
        accout.setAcctDiffCode("0");
        accout.setAcctTag("0");
        accout.setNetTypeCode("00");
        accout.setDepositPriorRuleId("0");
        accout.setItemPriorRuleId("0");
        accout.setOpenDate(userinfo.getString("OPEN_DATE", ""));
        accout.setRemoveTag(userinfo.getString("REMOVE_TAG", ""));
        accout.setRemark(acctinfo.getString("REMARK", ""));
        accout.setRsrvStr1(userinfo.getString("CUST_ID", ""));
        accout.setRsrvStr3(acctinfo.getString("ACCT_ID", ""));
        accout.setRsrvStr4(acctinfo.getString("RSRV_STR4", ""));
        accout.setRsrvStr5(acctinfo.getString("RSRV_STR5", ""));
        accout.setRsrvStr6(acctinfo.getString("RSRV_STR6", ""));
        accout.setRsrvStr7(acctinfo.getString("RSRV_STR7", ""));
        accout.setRsrvStr8(acctinfo.getString("RSRV_STR8", ""));
        accout.setRsrvStr9(acctinfo.getString("RSRV_STR9", ""));
        accout.setRsrvStr10(acctinfo.getString("RSRV_STR10", ""));

        bd.add(reqData.getUca().getSerialNumber(), accout);

    }

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {

        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) bd.getRD();
        String osn = reqData.getOtherSN();
        String useridA = SeqMgr.getUserId();
        IData ouserInfo = UcaInfoQry.qryUserInfoBySn(osn);

        IData userinfo = UcaInfoQry.qryUserInfoBySn(reqData.getSerialNum());
        IData custinfo = UcaInfoQry.qryCustomerInfoByCustId(userinfo.getString("CUST_ID", ""));
        IData acctinfo = UcaInfoQry.qryAcctInfoByUserId(userinfo.getString("USER_ID"));

        createMainTable(bd, ouserInfo);
        createUserTradeData(bd, userinfo, useridA);
        // createAcctTradeData(bd,userinfo,custinfo,acctinfo);
        // createCustPersonTradeData(bd,custinfo);
        // createCustomerTradeData(bd,custinfo,acctinfo,userinfo);
        createRelationTradeData(bd, useridA, ouserInfo);
        createDiscntTradeData(bd, useridA);
//        createVritualUserAcctDayTradeData(bd, useridA);
        // 账单日
        bd.addOpenUserAcctDayData(reqData.getUca().getUserId(), reqData.getUca().getAcctDay());
        bd.addOpenAccountAcctDayData(reqData.getUca().getAccount().getAcctId(), reqData.getUca().getAcctDay());

    }

    /**
     * @Function: createCustomerTradeData()
     * @Description:
     * @param:
     * @return：
     */
    public void createCustomerTradeData(BusiTradeData bd, IData custinfo, IData acctinfo, IData userinfo) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) bd.getRD();
        CustomerTradeData custTD = new CustomerTradeData();
        custTD.setCustId(custinfo.getString("CUST_ID", ""));
        custTD.setCustName(custinfo.getString("CUST_NAME", ""));
        custTD.setCustType("0");
        custTD.setCustState("0");
        custTD.setCustName(custinfo.getString("CUST_NAME", ""));
        custTD.setPsptTypeCode(custinfo.getString("PSPT_TYPE_CODE", ""));
        custTD.setPsptId(custinfo.getString("PSPT_ID", ""));
        custTD.setOpenLimit("0");
        custTD.setInDate(userinfo.getString("IN_DATE", ""));
        custTD.setRsrvStr1(acctinfo.getString("RSRV_STR1", ""));
        custTD.setRsrvStr2(acctinfo.getString("RSRV_STR2", ""));
        custTD.setRsrvStr3(acctinfo.getString("RSRV_STR3", ""));
        custTD.setRsrvStr4(acctinfo.getString("RSRV_STR4", ""));
        custTD.setRsrvStr5(acctinfo.getString("RSRV_STR5", ""));
        custTD.setRsrvStr6(acctinfo.getString("RSRV_STR6", ""));
        custTD.setRsrvStr7(acctinfo.getString("RSRV_STR7", ""));
        custTD.setRsrvStr8(acctinfo.getString("RSRV_STR8", ""));
        custTD.setRsrvStr9(acctinfo.getString("RSRV_STR9", ""));
        custTD.setRsrvStr10(acctinfo.getString("RSRV_STR10", ""));
        custTD.setEparchyCode(CSBizBean.getTradeEparchyCode());
        custTD.setCityCode(getVisit().getCityCode());
        custTD.setRemoveTag("0");
        custTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(reqData.getUca().getSerialNumber(), custTD);

    }

    /**
     * @Function: createCustPersonTradeData()
     * @Description:
     * @param:
     * @return：
     */
    public void createCustPersonTradeData(BusiTradeData bd, IData custinfo) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) bd.getRD();

        CustPersonTradeData cust = new CustPersonTradeData();
        cust.setCustId(custinfo.getString("CUST_ID", ""));
        cust.setPsptId(custinfo.getString("PSPT_ID", ""));
        cust.setPsptAddr(custinfo.getString("PSPT_ADDR", ""));
        cust.setCustName(custinfo.getString("CUST_NAME", ""));
        cust.setSex(custinfo.getString("SEX", ""));
        cust.setBirthday(custinfo.getString("BIRTHDAY", ""));
        cust.setRemoveTag("0");
        cust.setPsptTypeCode(reqData.getUca().getCustPerson().getPsptTypeCode());
        cust.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(reqData.getSerialNum(), cust);
    }

    /**
     * @Function: createDiscntTradeData()
     * @Description: 优惠子台帐
     * @param:
     * @return：
     */
    private void createDiscntTradeData(BusiTradeData btd, String userIdA) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) btd.getRD();
        //主卡userid挂848优惠
        DiscntTradeData discntTradeData = new DiscntTradeData();
        discntTradeData.setUserId(reqData.getUca().getUserId());
        discntTradeData.setUserIdA(userIdA);
        discntTradeData.setPackageId("-1");
        discntTradeData.setProductId("-1");
        discntTradeData.setSpecTag("2");
        discntTradeData.setRelationTypeCode("30");
        discntTradeData.setEndDate(SysDateMgr.END_TIME_FOREVER);
        discntTradeData.setStartDate(SysDateMgr.getSysDate());
        discntTradeData.setModifyTag("0");
        discntTradeData.setInstId(SeqMgr.getInstId());
        discntTradeData.setElementId("848");
        discntTradeData.setElementType("D");
        btd.add(reqData.getUca().getSerialNumber(), discntTradeData);
        
        /**虚拟用户挂5903 申请时，不应该给虚拟用户新增绑定  5903 优惠，应该在付费关系绑定时给主卡新增；
        DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(userIdA);
        newDiscnt.setUserIdA("-1");
        newDiscnt.setPackageId("-1");
        newDiscnt.setProductId("-1");
        newDiscnt.setSpecTag("2");
        newDiscnt.setRelationTypeCode("30");
        newDiscnt.setEndDate(SysDateMgr.END_TIME_FOREVER);
        newDiscnt.setStartDate(SysDateMgr.getSysDate());
        newDiscnt.setModifyTag("0");
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setElementId("5903");
        newDiscnt.setElementType("D");
        btd.add(reqData.getUca().getSerialNumber(), newDiscnt);
        */
    }

    /**
     * 主台账
     * 
     * @param bd
     * @throws Exception
     */
    public void createMainTable(BusiTradeData bd, IData ouserInfo) throws Exception
    {
        OneCardNCodesChangeCardReqData repData = (OneCardNCodesChangeCardReqData) bd.getRD();

        MainTradeData tradeData = bd.getMainTradeData();
        tradeData.setSubscribeType("0");
        tradeData.setNextDealTag("0");
        // tradeData.setProductId(ouserInfo.getString("PRODUCT_ID"));
        // tradeData.setBrandCode(ouserInfo.getString("BRAND_CODE"));
        tradeData.setFeeState("0");
        tradeData.setOlcomTag("0");
        tradeData.setProcessTagSet(tradeData.getProcessTagSet());
        tradeData.setCancelTag("0");
        tradeData.setRsrvStr7(repData.getSimCardNoM());// 主SIM卡
        tradeData.setRsrvStr4(ouserInfo.getString("SERIAL_NUMBER"));// 副号码
        tradeData.setRsrvStr5(repData.getSimCardNoO());// 副SIM卡
    }

    /**
     * @Function: createRelationTradeData()
     * @Description: UU关系
     * @param:
     * @return：
     */
    private void createRelationTradeData(BusiTradeData btd, String userIdA, IData ouserInfo) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) btd.getRD();
        RelationTradeData tempRelaTradeData = new RelationTradeData();
        tempRelaTradeData.setInstId(SeqMgr.getInstId());
        tempRelaTradeData.setUserIdA(userIdA);
        tempRelaTradeData.setUserIdB(reqData.getUca().getUserId());
        tempRelaTradeData.setSerialNumberB(reqData.getSerialNum());
        tempRelaTradeData.setSerialNumberA(userIdA.substring(6));
        tempRelaTradeData.setRelationTypeCode("30");
        tempRelaTradeData.setRoleTypeCode("0");
        tempRelaTradeData.setRoleCodeA("0");
        tempRelaTradeData.setRoleCodeB("1");
        tempRelaTradeData.setOrderno("0");
        tempRelaTradeData.setEndDate(SysDateMgr.END_TIME_FOREVER);
        tempRelaTradeData.setStartDate(SysDateMgr.getSysDate());
        tempRelaTradeData.setModifyTag("0");
        btd.add(reqData.getSerialNum(), tempRelaTradeData);

        RelationTradeData otempRelaTradeData = new RelationTradeData();
        otempRelaTradeData.setInstId(SeqMgr.getInstId());
        otempRelaTradeData.setUserIdA(userIdA);
        otempRelaTradeData.setUserIdB(ouserInfo.getString("USER_ID"));
        otempRelaTradeData.setSerialNumberB(ouserInfo.getString("SERIAL_NUMBER"));
        otempRelaTradeData.setSerialNumberA(userIdA.substring(6));
        otempRelaTradeData.setRelationTypeCode("30");
        otempRelaTradeData.setRoleTypeCode("0");
        otempRelaTradeData.setRoleCodeA("0");
        otempRelaTradeData.setRoleCodeB("2");
        otempRelaTradeData.setOrderno("1");
        otempRelaTradeData.setShortCode("");
        otempRelaTradeData.setEndDate(SysDateMgr.END_TIME_FOREVER);
        otempRelaTradeData.setStartDate(SysDateMgr.getSysDate());
        otempRelaTradeData.setModifyTag("0");
        btd.add(ouserInfo.getString("SERIAL_NUMBER"), otempRelaTradeData);

    }

    /**
     * @Function: createUserTradeData()
     * @Description: 用户台帐
     * @param:
     * @return：
     */
    public void createUserTradeData(BusiTradeData bd, IData userinfo, String userIdA) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) bd.getRD();

        UserTradeData userTD = new UserTradeData();
        userTD.setUserId(userIdA);
        userTD.setCustId(userinfo.getString("CUST_ID", ""));
        userTD.setUsecustId(userinfo.getString("CUST_ID", ""));
        userTD.setCityCodeA("0");
        userTD.setUserTypeCode(userinfo.getString("USER_TYPE_CODE", ""));
        userTD.setUserStateCodeset(userinfo.getString("USER_STATE_CODESET", ""));
        userTD.setPrepayTag(userinfo.getString("PREPAY_TAG", ""));
        userTD.setMputeMonthFee(userinfo.getString("MPUTE_MONTH_FEE", ""));
        userTD.setInDate(userinfo.getString("IN_DATE", ""));
        userTD.setInStaffId(userinfo.getString("IN_STAFF_ID", ""));
        userTD.setInDepartId(userinfo.getString("IN_DEPART_ID", ""));
        userTD.setOpenMode(userinfo.getString("OPEN_MODE", ""));
        userTD.setOpenDate(userinfo.getString("OPEN_DATE", ""));
        userTD.setRemoveTag(userinfo.getString("REMOVE_TAG", ""));
        userTD.setEparchyCode(reqData.getUca().getUser().getEparchyCode());
        userTD.setCityCode(reqData.getUca().getUser().getCityCode());
        userTD.setSerialNumber("30"+userIdA.substring(8));
        userTD.setAcctTag(reqData.getUca().getUser().getAcctTag());
        userTD.setNetTypeCode("00");
        userTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(reqData.getUca().getSerialNumber(), userTD);
        
        
        // 建立用户产品信息
        ProductTradeData addProductTD = new ProductTradeData();
        addProductTD.setUserId(userIdA);
        addProductTD.setProductId("5500");
        addProductTD.setBrandCode("OCNC");
        addProductTD.setStartDate(SysDateMgr.getSysDate());
        addProductTD.setEndDate(SysDateMgr.getTheLastTime());
        addProductTD.setMainTag("1");
        addProductTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        addProductTD.setProductMode("04");
        addProductTD.setUserIdA("-1");
        addProductTD.setCampnId("0");
        addProductTD.setInstId(SeqMgr.getInstId());
        bd.add(reqData.getUca().getSerialNumber(), addProductTD);
    }

    /***************************************************************************************
     * 为虚拟主号生成用户账期数据
     * 
     * @param userIdNew
     *            虚拟号码USER_ID，对应RELATION_UU表中USER_ID_A
     * @throws Exception
     */
    private void createVritualUserAcctDayTradeData(BusiTradeData btd, String userIdA) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) btd.getRD();

        UserAcctDayTradeData userAcctData = new UserAcctDayTradeData();
        userAcctData.setUserId(userIdA);
        userAcctData.setChgType("1");
        userAcctData.setChgMode("0");
        userAcctData.setInstId(SeqMgr.getInstId());
        // TODO 分散账期 获取下账期的开始日期，条件存在td中
        userAcctData.setFirstDate(SysDateMgr.getFirstDayOfNextMonth());
        userAcctData.setRsrvTag1("1");
        userAcctData.setRemark("一卡双号申请业务生成用户帐期");
        userAcctData.setEndDate(SysDateMgr.END_TIME_FOREVER);
        userAcctData.setStartDate(SysDateMgr.getSysDate());
        userAcctData.setModifyTag("0");
        userAcctData.setAcctDay(reqData.getUca().getAcctDay());
        userAcctData.setChgDate(SysDateMgr.getSysDate());
        btd.add(reqData.getUca().getSerialNumber(), userAcctData);

    }
}
