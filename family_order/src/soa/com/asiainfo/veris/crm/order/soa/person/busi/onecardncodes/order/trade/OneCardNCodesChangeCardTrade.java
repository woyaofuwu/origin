/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order.requestdata.OneCardNCodesChangeCardReqData;

public class OneCardNCodesChangeCardTrade extends BaseTrade implements ITrade
{

    /**
     * @Function: createAcctTradeData()
     * @Description: 账户台帐
     * @param:
     * @return：
     */
    public void createAcctTradeData(BusiTradeData bd, IData ouserInfo, IData oacct, IData userinfo, IData acctinfo) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) bd.getRD();

        AccountTradeData accout = new AccountTradeData();
        accout.setAcctId(acctinfo.getString("ACCT_ID", ""));
        accout.setCustId(userinfo.getString("CUST_ID", ""));
        accout.setPayName(acctinfo.getString("PAY_NAME", ""));
        accout.setPayModeCode(acctinfo.getString("PAY_MODE_CODE", ""));
        accout.setModifyTag(BofConst.MODIFY_TAG_ADD);
        accout.setScoreValue(userinfo.getString("SCORE_VALUE", ""));
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

        AccountTradeData oaccout = new AccountTradeData(oacct);
        oaccout.setAcctId(oacct.getString("ACCT_ID", ""));
        oaccout.setCustId(ouserInfo.getString("CUST_ID", ""));
        oaccout.setPayName(oacct.getString("PAY_NAME", ""));
        oaccout.setPayModeCode(oacct.getString("PAY_MODE_CODE", ""));
        oaccout.setModifyTag(BofConst.MODIFY_TAG_ADD);
        oaccout.setScoreValue(ouserInfo.getString("SCORE_VALUE", ""));
        oaccout.setAcctDiffCode("0");
        oaccout.setAcctTag("0");
        oaccout.setNetTypeCode("00");
        oaccout.setDepositPriorRuleId("0");
        oaccout.setItemPriorRuleId("0");
        oaccout.setOpenDate(ouserInfo.getString("OPEN_DATE", ""));
        oaccout.setRemoveTag(ouserInfo.getString("REMOVE_TAG", ""));
        oaccout.setRemark(oacct.getString("REMARK", ""));
        oaccout.setRsrvStr1(oacct.getString("CUST_ID", ""));
        oaccout.setRsrvStr3(oacct.getString("ACCT_ID", ""));
        oaccout.setRsrvStr4(oacct.getString("RSRV_STR4", ""));
        oaccout.setRsrvStr5(oacct.getString("RSRV_STR5", ""));
        oaccout.setRsrvStr6(oacct.getString("RSRV_STR6", ""));
        oaccout.setRsrvStr7(oacct.getString("RSRV_STR7", ""));
        oaccout.setRsrvStr8(oacct.getString("RSRV_STR8", ""));
        oaccout.setRsrvStr9(oacct.getString("RSRV_STR9", ""));
        oaccout.setRsrvStr10(oacct.getString("RSRV_STR10", ""));
        bd.add(ouserInfo.getString("SERIAL_NUMBER", ""), oaccout);
    }

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {

        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) bd.getRD();

        String sn = reqData.getSerialNum();
//        String osn = reqData.getOtherSN();

//        IData ouserInfo = UserInfoQry.getUserInfoBySN(osn);
//        IData ocust = UcaInfoQry.qryPerInfoByUserId(ouserInfo.getString("USER_ID"));

//        IData ocustm = UcaInfoQry.qryCustomerInfoByCustId(ouserInfo.getString("CUST_ID"), bd.getRoute());
//        IData oacct = UAcctInfoQry.qryAcctInfoByCustId(ouserInfo.getString("CUST_ID")).getData(0);

        IData userinfo = UcaInfoQry.qryUserInfoBySn(sn);
        IData custinfo = UcaInfoQry.qryCustInfoByCustId(userinfo.getString("CUST_ID"));
        IData acctinfo = UcaInfoQry.qryAcctInfoByUserId(userinfo.getString("USER_ID"));

        // 资源预占
        if(reqData.isTag()){
            ResCall.ocncPreOccupy(reqData.getSimCardNoM(), reqData.getSimCardNoO(), reqData.getSerialNum());
        }
//        ResCall.resEngrossForSim("1",reqData.getSimCardNoM() , sn);
        
        createMainTable(bd);
        // createAcctTradeData(bd, ouserInfo,oacct,userinfo,acctinfo);
        // createCustPersonTradeData(bd, ocust,custinfo);
        // createUserTradeData(bd, ouserInfo,userinfo);
        // createCustomerTradeData(bd, ouserInfo,ocust,oacct,userinfo,custinfo,acctinfo);
        createResTradeData(bd);
        bd.addOpenUserAcctDayData(reqData.getUca().getUserId(), reqData.getUca().getAcctDay());
        bd.addOpenAccountAcctDayData(reqData.getUca().getAccount().getAcctId(), reqData.getUca().getAcctDay());
        // 占用
        if(reqData.isTag()){
            ResCall.ocncUseOccupy(reqData.getSimCardNoM(), reqData.getSimCardNoO(), reqData.getSerialNum(),reqData.getOtherSN(),reqData.getTradeId(),reqData.getUca().getUserId());
        }
        // 资源信息占用
//        ResCall.changeSimCardFinish("1", reqData.getSerialNum(), reqData.getSimCardNoM(), "0", reqData.getTradeId(), "321", "1", reqData.getUca().getUserId(), "0", "0", reqData.getUca().getProductId(),"00" );    
  
    }

    /**
     * @Function: createCustomerTradeData()
     * @Description:
     * @param:
     * @return：
     */
    public void createCustomerTradeData(BusiTradeData bd, IData ouserInfo, IData ocust, IData oacct, IData userinfo, IData custinfo, IData acctinfo) throws Exception
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
        custTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(reqData.getUca().getSerialNumber(), custTD);

        CustomerTradeData ocustTD = new CustomerTradeData();
        ocustTD.setCustId(ocust.getString("CUST_ID", ""));
        ocustTD.setCustName(ocust.getString("CUST_NAME", ""));
        ocustTD.setCustType("0");
        ocustTD.setCustState("0");
        ocustTD.setCustName(ocust.getString("CUST_NAME", ""));
        ocustTD.setPsptTypeCode(ocust.getString("PSPT_TYPE_CODE", ""));
        ocustTD.setPsptId(ocust.getString("PSPT_ID", ""));
        ocustTD.setOpenLimit("0");
        ocustTD.setInDate(ouserInfo.getString("IN_DATE", ""));
        ocustTD.setRsrvStr1(oacct.getString("RSRV_STR1", ""));
        ocustTD.setRsrvStr2(oacct.getString("RSRV_STR2", ""));
        ocustTD.setRsrvStr3(oacct.getString("RSRV_STR3", ""));
        ocustTD.setRsrvStr4(oacct.getString("RSRV_STR4", ""));
        ocustTD.setRsrvStr5(oacct.getString("RSRV_STR5", ""));
        ocustTD.setRsrvStr6(oacct.getString("RSRV_STR6", ""));
        ocustTD.setRsrvStr7(oacct.getString("RSRV_STR7", ""));
        ocustTD.setRsrvStr8(oacct.getString("RSRV_STR8", ""));
        ocustTD.setRsrvStr9(oacct.getString("RSRV_STR9", ""));
        ocustTD.setRsrvStr10(oacct.getString("RSRV_STR10", ""));
        ocustTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(reqData.getUca().getSerialNumber(), ocustTD);
    }

    /**
     * @Function: createCustPersonTradeData()
     * @Description:
     * @param:
     * @return：
     */
    public void createCustPersonTradeData(BusiTradeData bd, IData ocust, IData custinfo) throws Exception
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
        cust.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(reqData.getSerialNum(), cust);

        CustPersonTradeData ocustper = new CustPersonTradeData();
        ocustper.setCustId(ocust.getString("CUST_ID", ""));
        ocustper.setPsptId(ocust.getString("PSPT_ID", ""));
        ocustper.setPsptAddr(ocust.getString("PSPT_ADDR", ""));
        ocustper.setCustName(ocust.getString("CUST_NAME", ""));
        ocustper.setSex(ocust.getString("SEX", ""));
        ocustper.setBirthday(ocust.getString("BIRTHDAY", ""));
        ocustper.setRemoveTag("0");
        ocustper.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(reqData.getSerialNumO(), ocustper);

    }

    /**
     * 主台账
     * 
     * @param bd
     * @throws Exception
     */
    public void createMainTable(BusiTradeData bd) throws Exception
    {
        OneCardNCodesChangeCardReqData repData = (OneCardNCodesChangeCardReqData) bd.getRD();

        MainTradeData tradeData = bd.getMainTradeData();
        tradeData.setOlcomTag("1");
        tradeData.setSubscribeType("0");
        tradeData.setSubscribeState("0");
        tradeData.setNextDealTag("0");
        if(tradeData.getOperFee() != "0" || tradeData.getForegift() !="0" || tradeData.getAdvancePay() != "0"){
        	tradeData.setFeeState("1");
        }else{
        	tradeData.setFeeState("0");
        }
        tradeData.setProcessTagSet("0000000000000000000E");
        tradeData.setCancelTag("0");
        tradeData.setRsrvStr7(repData.getSimCardNoM());// 主SIM卡
        if(repData.isTag()){
            tradeData.setRsrvStr4("0");// 主号码标记
        }else{
            tradeData.setRsrvStr4("1");// 副号码标记
        }
//        tradeData.setRsrvStr4(repData.getOtherSN());// 副号码
//        tradeData.setRsrvStr5(repData.getSimCardNoO());// 副SIM卡

    }

    /**
     * @Function: createResTradeData()
     * @Description: 资源锚台帐
     * @param:
     * @return：
     */
    private void createResTradeData(BusiTradeData btd) throws Exception
    {
        // TODO
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) btd.getRD();

        // 主卡删除一条 新增一条
        IDataset oldSimInfos = UserResInfoQry.getUserResInfosByUserIdResTypeCode(reqData.getUca().getUserId(), "1");
        ResTradeData resTradeDataOld = new ResTradeData(oldSimInfos.getData(0));
        resTradeDataOld.setModifyTag(BofConst.MODIFY_TAG_DEL);
        resTradeDataOld.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
        if(reqData.isTag()){
        	resTradeDataOld.setRsrvTag1("0");// 主号码标记
        }else{
        	resTradeDataOld.setRsrvTag1("1");// 副号码标记
        }
        btd.add(reqData.getUca().getSerialNumber(), resTradeDataOld);

        ResTradeData resTradeDataNew = new ResTradeData();
        String instId = SeqMgr.getInstId();
        resTradeDataNew.setUserId(reqData.getUca().getUserId());
        resTradeDataNew.setUserIdA("-1");
        resTradeDataNew.setResTypeCode("1");
        resTradeDataNew.setResCode(reqData.getSimCardNoM());
        resTradeDataNew.setInstId(instId);
        resTradeDataNew.setImsi(reqData.getImsi());
        resTradeDataNew.setKi(reqData.getKi_a());
        resTradeDataNew.setStartDate(btd.getRD().getAcceptTime());
        resTradeDataNew.setEndDate(SysDateMgr.END_DATE_FOREVER);
        resTradeDataNew.setModifyTag(BofConst.MODIFY_TAG_ADD);
        if(reqData.isTag()){
        	resTradeDataNew.setRsrvTag1("0");// 主号码标记
        	resTradeDataNew.setRsrvTag2("B");
        	String simCardNo = reqData.getSimCardNoM();
        	IDataset resInfos = ResCall.getSimCardInfo("0",simCardNo, "", "");
        	if(IDataUtil.isNotEmpty(resInfos)){
        		String emptyCardId = resInfos.getData(0).getString("EMPTY_CARD_ID");
        		if(StringUtils.isNotEmpty(emptyCardId)){
        			IDataset empInfos = ResCall.getEmptycardInfo(emptyCardId, "","" );
        			if(IDataUtil.isNotEmpty(empInfos)){
        				if("3".equals(empInfos.getData(0).getString("RSRV_TAG1",""))){
        					resTradeDataNew.setRsrvTag2("A");
        					resTradeDataNew.setRsrvNum5(empInfos.getData(0).getString("RSRV_NUM1","0"));
        				}
        			}
        		}
        	}
        }else{
        	resTradeDataNew.setRsrvTag1("1");// 副号码标记
        }
        btd.add(reqData.getUca().getSerialNumber(), resTradeDataNew);
               
//        // 副卡删除一条 新增一条
//        IDataset oldSimInfosOther = UserResInfoQry.getUserResInfosByUserIdResTypeCode(ouser.getString("USER_ID"), "1");
//        ResTradeData resTradeDataOldOther = new ResTradeData(oldSimInfosOther.getData(0));
//        resTradeDataOldOther.setModifyTag(BofConst.MODIFY_TAG_DEL);
//        resTradeDataOldOther.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
//        resTradeDataOldOther.setRsrvTag1("1");
//        btd.add(reqData.getSerialNumO(), resTradeDataOldOther);
//
//        ResTradeData resTradeDataNewOther = new ResTradeData();
//        String instId2 = SeqMgr.getInstId();
//        resTradeDataNewOther.setUserId(ouser.getString("USER_ID"));
//        resTradeDataNewOther.setUserIdA("-1");
//        resTradeDataNewOther.setResTypeCode("1");
//        resTradeDataNewOther.setResCode(reqData.getSimCardNoO());
//        resTradeDataNewOther.setInstId(instId2);
//        resTradeDataNewOther.setImsi(reqData.getImsiO());
//        resTradeDataNewOther.setKi(reqData.getKi_b());
//        resTradeDataNewOther.setStartDate(btd.getRD().getAcceptTime());
//        resTradeDataNewOther.setEndDate(SysDateMgr.END_DATE_FOREVER);
//        resTradeDataNewOther.setModifyTag(BofConst.MODIFY_TAG_ADD);
//        resTradeDataNewOther.setRsrvTag1("1");
//        btd.add(reqData.getSerialNumO(), resTradeDataNewOther);

        // String serialNumber = reqData.getUca().getUser().getSerialNumber();
        // ResTradeData resTD = new ResTradeData();
        // String inst_id = SeqMgr.getInstId();
        // resTD.setUserId(reqData.getUca().getUserId());
        // resTD.setUserIdA("-1");
        // resTD.setResTypeCode("W");
        // // resTD.setResCode(reqData.getModemCode());
        // resTD.setImsi("");
        // resTD.setKi("");
        // resTD.setInstId(inst_id);
        // resTD.setStartDate(reqData.getUca().getUser().getOpenDate());
        // resTD.setEndDate(SysDateMgr.getTheLastTime());
        // resTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        // // resTD.setRsrvStr1(reqData.getModemNumberic()); // modem型号
        // resTD.setRsrvStr4("03"); // modem的小类编码
        // // resTD.setRsrvTag1(reqData.getModemStyle()); // modem方式
        // btd.add(serialNumber, resTD);
    }

    /**
     * @Function: createUserTradeData()
     * @Description: 用户台帐
     * @param:
     * @return：
     */
    public void createUserTradeData(BusiTradeData bd, IData ouserInfo, IData userinfo) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) bd.getRD();

        UserTradeData userTD = new UserTradeData();
        userTD.setUserId(userinfo.getString("USER_ID", ""));
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
        userTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(reqData.getUca().getSerialNumber(), userTD);

        UserTradeData ouserTD = new UserTradeData();
        ouserTD.setUserId(ouserInfo.getString("USER_ID", ""));
        ouserTD.setCustId(ouserInfo.getString("CUST_ID", ""));
        ouserTD.setUsecustId(ouserInfo.getString("CUST_ID", ""));
        ouserTD.setCityCodeA("0");
        ouserTD.setUserTypeCode(ouserInfo.getString("USER_TYPE_CODE", ""));
        ouserTD.setUserStateCodeset(ouserInfo.getString("USER_STATE_CODESET", ""));
        ouserTD.setPrepayTag(ouserInfo.getString("PREPAY_TAG", ""));
        ouserTD.setMputeMonthFee(ouserInfo.getString("MPUTE_MONTH_FEE", ""));
        ouserTD.setInDate(ouserInfo.getString("IN_DATE", ""));
        ouserTD.setInStaffId(ouserInfo.getString("IN_STAFF_ID", ""));
        ouserTD.setInDepartId(ouserInfo.getString("IN_DEPART_ID", ""));
        ouserTD.setOpenMode(ouserInfo.getString("OPEN_MODE", ""));
        ouserTD.setOpenDate(ouserInfo.getString("OPEN_DATE", ""));
        ouserTD.setRemoveTag(ouserInfo.getString("REMOVE_TAG", ""));
        ouserTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(reqData.getSerialNumO(), ouserTD);

    }
}
