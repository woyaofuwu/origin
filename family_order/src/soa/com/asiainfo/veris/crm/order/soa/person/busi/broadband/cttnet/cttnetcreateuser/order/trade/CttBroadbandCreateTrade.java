
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order.trade;

import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctConsignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AddrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetActTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.BankInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order.requestdata.CttBroadbandCreateReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.trade.CreateUserTrade;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CttBroadbandCreateTrade.java
 * @Description:
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-3-13 上午11:09:44 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-13 yxd v1.0.0 修改原因
 */
public class CttBroadbandCreateTrade extends CreateUserTrade implements ITrade
{

    /**
     * @Function: createAcctConsignTradeData()
     * @Description: 托收账户台帐
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-3-18 上午9:45:25 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-18 yxd v1.0.0 修改原因
     */
    private void createAcctConsignTradeData(BusiTradeData btd) throws Exception
    {
        AcctConsignTradeData acctConsignTD = new AcctConsignTradeData();
        CttBroadbandCreateReqData reqData = (CttBroadbandCreateReqData) btd.getRD();
        UcaData ucaData = reqData.getUca();
        AccountTradeData accountTradeData = ucaData.getAccount();
        String serialNumber = ucaData.getUser().getSerialNumber();

        acctConsignTD.setAcctId(ucaData.getAcctId());
        acctConsignTD.setPayModeCode(accountTradeData.getPayModeCode());// 帐户付费类型：0-现金，1-托收，2-代扣
        acctConsignTD.setEparchyCode(BizRoute.getRouteId());
        acctConsignTD.setCityCode(accountTradeData.getCityCode());
        acctConsignTD.setSuperBankCode(reqData.getSuperBankCode());
        acctConsignTD.setBankCode(accountTradeData.getBankCode());
        acctConsignTD.setBankAcctNo(accountTradeData.getBankAcctNo());
        // String bankName = BankInfoQry.getBankNameByBankCode(reqData.getUca().getAccount().getBankCode());
        String bankName = accountTradeData.getBankCode();
        IDataset bankNameList = BankInfoQry.getBankByBankCtt(CSBizBean.getTradeEparchyCode(), reqData.getSuperBankCode(), accountTradeData.getBankCode(), accountTradeData.getBankCode());
        if (IDataUtil.isNotEmpty(bankNameList))
        {
            bankName = bankNameList.getData(0).getString("BANK");
        }
        acctConsignTD.setBankAcctName(bankName);
        acctConsignTD.setConsignMode("1");// 托收方式：默认为1
        acctConsignTD.setPaymentId("4");// 储值方式：默认为4
        acctConsignTD.setPayFeeModeCode("4");
        acctConsignTD.setActTag("1");
        acctConsignTD.setInstId(SeqMgr.getInstId());
        acctConsignTD.setStartCycleId(SysDateMgr.getNowCyc());
        acctConsignTD.setEndCycleId(SysDateMgr.getEndCycle205012());
        acctConsignTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(serialNumber, acctConsignTD);
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {

        CttBroadbandCreateReqData reqData = (CttBroadbandCreateReqData) btd.getRD();

        super.createBusiTradeData(btd);
        createPayrelationTradeData(btd);
        createCustomerTradeData(btd);// 处理客户核心资料
        // createTradeAccessAcct(btd, reqData);
        // createTradeAddr(btd,reqData);
        // createTradeRate(btd,reqData);
        getTradeWidenetData(btd);// 宽带用户资料
        getTradeWidenetAcctData(btd);
        // 托收账户台帐
        if ("1".equals(reqData.getUca().getAccount().getPayModeCode()))
        {
            createAcctConsignTradeData(btd);
        }

        createMainTradeData(btd);
    }
    
    /**
     * 客户资料表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createCustomerTradeData(BusiTradeData btd) throws Exception
    {
    	CttBroadbandCreateReqData reqData = (CttBroadbandCreateReqData) btd.getRD();

        List<CustomerTradeData> customerTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_CUSTOMER);
        for (CustomerTradeData customerTradeData : customerTradeDatas)
        {
            customerTradeData.setRsrvStr7(reqData.getAgentCustName());// 经办人名称
            customerTradeData.setRsrvStr8(reqData.getAgentPsptTypeCode());// 经办人证件类型
            customerTradeData.setRsrvStr9(reqData.getAgentPsptId());// 经办人证件号码
            customerTradeData.setRsrvStr10(reqData.getAgentPsptAddr());// 经办人证件地址
        }
    }

    public void createMainTradeData(BusiTradeData btd) throws Exception
    {
        CttBroadbandCreateReqData reqData = (CttBroadbandCreateReqData) btd.getRD();
        CustomerTradeData cust = reqData.getUca().getCustomer();
        MainTradeData mainTradeData = btd.getMainTradeData();

        mainTradeData.setSubscribeType(CttConstants.SUBSCRIBE_TYPE);
        mainTradeData.setPfType(CttConstants.PF_TYPE);
        mainTradeData.setOlcomTag(CttConstants.OLCOM_TAG);
        mainTradeData.setCityCode(CSBizBean.getVisit().getCityCode());
        mainTradeData.setRsrvStr1(cust.getPsptTypeCode());
        mainTradeData.setRsrvStr2(cust.getPsptId());

    }

    /**
     * @Function: createPayrelationTradeData()
     * @Description: 支付关系台帐
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-3-18 上午9:45:37 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-18 yxd v1.0.0 修改原因
     */
    private void createPayrelationTradeData(BusiTradeData btd) throws Exception
    {
        PayRelationTradeData payrelationTD = new PayRelationTradeData();
        CttBroadbandCreateReqData reqData = (CttBroadbandCreateReqData) btd.getRD();
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        payrelationTD.setUserId(reqData.getUca().getUserId());
        payrelationTD.setAcctId(reqData.getUca().getAcctId());
        payrelationTD.setPayitemCode("-1");
        payrelationTD.setAcctPriority("0");
        payrelationTD.setUserPriority("0");
        payrelationTD.setBindType("1");
        payrelationTD.setStartCycleId(SysDateMgr.getNowCycle());
        payrelationTD.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payrelationTD.setActTag("1");
        payrelationTD.setDefaultTag("1");
        payrelationTD.setLimitType("0");
        payrelationTD.setLimit("0");
        payrelationTD.setComplementTag("0");
        payrelationTD.setRemark(reqData.getRemark());
        payrelationTD.setInstId(SeqMgr.getInstId());
        payrelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(serialNumber, payrelationTD);
    }

    /**
     * @Function: createResTradeData()
     * @Description: 资源锚台帐
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-3-18 上午9:45:44 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-18 yxd v1.0.0 修改原因
     */
    public void createResTradeData(BusiTradeData btd) throws Exception
    {

        CttBroadbandCreateReqData reqData = (CttBroadbandCreateReqData) btd.getRD();
        if (StringUtils.isNotEmpty(reqData.getModemNumberic()))
        {
            String resCode = StringUtils.equals("", reqData.getModemCode()) ? "-1" : reqData.getModemCode();
            String serialNumber = reqData.getUca().getUser().getSerialNumber();
            ResTradeData resTD = new ResTradeData();
            String inst_id = SeqMgr.getInstId();
            resTD.setUserId(reqData.getUca().getUserId());
            resTD.setUserIdA("-1");
            resTD.setResTypeCode("W");
            resTD.setResCode(resCode);
            resTD.setImsi("");
            resTD.setKi("");
            resTD.setInstId(inst_id);
            resTD.setStartDate(reqData.getAcceptTime());
            resTD.setEndDate(SysDateMgr.getTheLastTime());
            resTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            resTD.setRsrvStr1(reqData.getModemNumberic()); // modem型号
            resTD.setRsrvStr2(reqData.getModemNumbericDesc());
            resTD.setRsrvStr3(StaticUtil.getStaticValue("BROADBAND_ASSET_STYLE", reqData.getModemStyle()));
            resTD.setRsrvStr4("03"); // modem的小类编码
            resTD.setRsrvTag1(reqData.getModemStyle()); // modem方式
            btd.add(serialNumber, resTD);
        }

    }

    /**
     * @Function: createTradeAccessAcct()
     * @Description: 宽带帐号台帐
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-3-18 上午9:44:00 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-18 yxd v1.0.0 修改原因
     */
    private void createTradeAccessAcct(BusiTradeData btd, CttBroadbandCreateReqData reqData) throws Exception
    {
        // AccessAcctTradeData accessAcctTD = new AccessAcctTradeData();
        WideNetTradeData accessAcctTD = new WideNetTradeData();
        // accessAcctTD.setSerialNumber(reqData.getUca().getSerialNumber());
        accessAcctTD.setUserId(reqData.getUca().getUserId());
        accessAcctTD.setInstId(SeqMgr.getInstId());
        // accessAcctTD.setAccessAcct(reqData.getBroadBandAcctId());
        // accessAcctTD.setAccessType(reqData.getConnectType());
        // accessAcctTD.setAccessPwd(reqData.getBroadBandPwd());
        accessAcctTD.setStartDate(SysDateMgr.getSysDate());
        accessAcctTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
        accessAcctTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        accessAcctTD.setRsrvStr1("0");
        accessAcctTD.setRemark("铁通宽带");

        btd.add(btd.getRD().getUca().getSerialNumber(), accessAcctTD);
    }

    /**
     * @Function: createTradeAddr()
     * @Description: 宽带地址台帐
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-3-18 上午9:48:26 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-18 yxd v1.0.0 修改原因
     */
    private void createTradeAddr(BusiTradeData btd, CttBroadbandCreateReqData reqData) throws Exception
    {
        AddrTradeData addrTD = new AddrTradeData();
        addrTD.setUserId(reqData.getUca().getUserId());
        addrTD.setInstId(SeqMgr.getInstId());
        addrTD.setMofficeId(reqData.getMofficeId());
        addrTD.setAddrName(reqData.getStandAddress());
        addrTD.setAddrId(reqData.getAddrDesc());
        addrTD.setAddrDesc(reqData.getAddrDesc());
        addrTD.setStartDate(SysDateMgr.getSysDate());
        addrTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
        addrTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        addrTD.setRemark("铁通宽带");

        btd.add(btd.getRD().getUca().getSerialNumber(), addrTD);
    }

    /**
     * @Function: createTradeRate()
     * @Description: 速率
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-3-18 上午9:52:55 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-18 yxd v1.0.0 修改原因
     */
    private void createTradeRate(BusiTradeData btd, CttBroadbandCreateReqData reqData) throws Exception
    {
        RateTradeData rateTD = new RateTradeData();
        rateTD.setUserId(reqData.getUca().getUserId());
        rateTD.setInstId(SeqMgr.getInstId());
        rateTD.setStartDate(SysDateMgr.getSysDate());
        rateTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
        rateTD.setRate(reqData.getRate());
        rateTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        rateTD.setRemark("铁通宽带");

        btd.add(btd.getRD().getUca().getSerialNumber(), rateTD);
    }

    /**
     * 登记宽带用户账户台帐
     * 
     * @return
     * @throws Exception
     */
    public void getTradeWidenetAcctData(BusiTradeData btd) throws Exception
    {
        CttBroadbandCreateReqData reqData = (CttBroadbandCreateReqData) btd.getRD();
        UcaData ucaData = reqData.getUca();

        WideNetActTradeData widenetActTradeData = new WideNetActTradeData();

        widenetActTradeData.setUserId(ucaData.getUserId());
        widenetActTradeData.setAcctId(reqData.getBroadBandAcctId());
        widenetActTradeData.setAcctPasswd(reqData.getBroadBandPwd());
        widenetActTradeData.setMainTag("0");
        widenetActTradeData.setInstId(SeqMgr.getInstId());
        widenetActTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        widenetActTradeData.setStartDate(reqData.getAcceptTime());
        widenetActTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        widenetActTradeData.setRsrvStr1(reqData.getWidenetIpAddr());// 军区用户IP地址
        btd.add(ucaData.getSerialNumber(), widenetActTradeData);
    }

    public void getTradeWidenetData(BusiTradeData btd) throws Exception
    {

        CttBroadbandCreateReqData reqData = (CttBroadbandCreateReqData) btd.getRD();
        UcaData ucaData = reqData.getUca();

        WideNetTradeData widenetTradeData = new WideNetTradeData();

        widenetTradeData.setInstId(SeqMgr.getInstId());
        widenetTradeData.setUserId(ucaData.getUserId());
        widenetTradeData.setStandAddress(reqData.getStandAddress()); // 标准地址
        widenetTradeData.setStandAddressCode(reqData.getStandAddressCode());
        widenetTradeData.setDetailAddress(reqData.getAddrDesc()); // 安装地址
        widenetTradeData.setPhone(reqData.getPhone());
        widenetTradeData.setContact(reqData.getContact());
        widenetTradeData.setAcctPasswd(reqData.getBroadBandPwd());
        widenetTradeData.setContactPhone(reqData.getContactPhone());
        widenetTradeData.setSignPath(reqData.getMofficeId());
        widenetTradeData.setPortType("");
        widenetTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        widenetTradeData.setStartDate(reqData.getAcceptTime());
        widenetTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        widenetTradeData.setRsrvStr1(reqData.getConnectType());
        widenetTradeData.setRsrvStr5(reqData.getCttPhone());

        btd.add(ucaData.getSerialNumber(), widenetTradeData);
    }
}
