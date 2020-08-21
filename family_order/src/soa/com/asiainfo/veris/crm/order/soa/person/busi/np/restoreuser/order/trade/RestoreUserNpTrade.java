
package com.asiainfo.veris.crm.order.soa.person.busi.np.restoreuser.order.trade;

import java.util.Date;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.NpTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.requestdata.ResInfo;
import com.asiainfo.veris.crm.order.soa.person.busi.np.restoreuser.RestoreUserNpBean;
import com.asiainfo.veris.crm.order.soa.person.busi.np.restoreuser.order.requestdata.RestoreUserNpReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RestoreUserNpTrade.java
 * @Description: 能够办理携号复机的号码，1.必须是携出用户（4），2- 携入携出：针对携入的用户超过三月后再携入到其他的运营商时状态置为“携入已出”（8）
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-6-10 下午3:22:54 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-6-10 lijm3 v1.0.0 修改原因
 */
public class RestoreUserNpTrade extends BaseTrade implements ITrade
{

    /**
     * @Function: createAttrTradeData
     * @Description: 处理USIM卡的OPC值
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年8月6日 下午4:45:31
     */
    private void createAttrTradeData(BusiTradeData btd) throws Exception
    {
        RestoreUserNpReqData reqData = (RestoreUserNpReqData) btd.getRD();
        List<ResInfo> resInfos = reqData.getResInfos();
        for (ResInfo resInfo : resInfos)
        {
            if ("1".equals(resInfo.getResTypeCode()) && StringUtils.isNotBlank(resInfo.getOpc()))
            {
                AttrTradeData atd = new AttrTradeData();
                atd.setAttrCode("OPC_VALUE");
                atd.setAttrValue(resInfo.getOpc());
                atd.setInstType("R");
                atd.setInstId(SeqMgr.getInstId());
                atd.setModifyTag(BofConst.MODIFY_TAG_ADD);
                atd.setStartDate(reqData.getAcceptTime());
                atd.setEndDate(SysDateMgr.END_DATE_FOREVER);
                atd.setRelaInstId(SeqMgr.getInstId());
                atd.setUserId(reqData.getUca().getUserId());
                btd.add(reqData.getUca().getSerialNumber(), atd);
            }
        }
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {

        RestoreUserNpReqData reqData = (RestoreUserNpReqData) btd.getRD();

        int day = SysDateMgr.dayInterval(reqData.getUca().getUser().getDestroyTime(), SysDateMgr.getSysTime());
        String exceedNinety = "";
        if (day <= 90)
        {
            exceedNinety = "0";
        }
        else
        {
            exceedNinety = "1";
        }

        createUserAndUserNpTradeData(btd);

        createProductTradeData(btd);
        ProductModuleCreator.createProductModuleTradeData(reqData.getPmds(), btd);
        // CreateUserSvcStateTradeData(btd);

        createResTradeData(btd);

        createPayRelaTradeData(btd);// 老系没有恢复付费关系，但是携出生效的代码是终止付费关系的

        String newSim = "";
        List<ResInfo> resInfos = reqData.getResInfos();
        if (resInfos != null)
        {
            for (ResInfo resInfo : resInfos)
            {
                if ("1".equals(resInfo.getResTypeCode()))
                {
                    newSim = resInfo.getResCode();
                    break;
                }
            }
        }

        btd.getMainTradeData().setRsrvStr10("1");// 预留字段10，1：更换产品
        btd.getMainTradeData().setRsrvStr9(exceedNinety);// 销户是否超过90天 0-不超过 1-超过
        btd.getMainTradeData().setRsrvStr8("");// 卡类型
        btd.getMainTradeData().setRsrvStr7(newSim);// 新sim卡
        btd.getMainTradeData().setRsrvStr3("1");// 是否修改sim卡 0-不修改 1-修改
        btd.getMainTradeData().setRsrvStr2(reqData.getUca().getSerialNumber());// 新手机号码
        btd.getMainTradeData().setOlcomTag("1");// 联指标记
        if (StringUtils.isNotBlank(reqData.getInvoiceNo()))
        {
            btd.getMainTradeData().setInvoiceNo(reqData.getInvoiceNo());
        }

        createAttrTradeData(btd);
        btd.getMainTradeData().setExecTime(SysDateMgr.END_DATE_FOREVER);
        modifyUserMainSvcStateGroups(btd);
    }

    /**
     * @Function: createCreditTradeData
     * @Description: 信用度子表
     * @param btd
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月15日 上午11:10:01
     */
    public void createCreditTradeData(BusiTradeData btd) throws Exception
    {
        RestoreUserNpReqData reqData = (RestoreUserNpReqData) btd.getRD();
        String serialNumber = reqData.getUca().getUser().getSerialNumber();

        CreditTradeData ctd = new CreditTradeData();
        ctd.setUserId(reqData.getUca().getUserId());
        ctd.setCreditValue("2000");
        ctd.setCreditMode("1");
        ctd.setCreditGiftMonths("0");
        ctd.setStartDate(reqData.getAcceptTime());
        ctd.setEndDate(SysDateMgr.addDays(reqData.getAcceptTime(), 3));
        ctd.setModifyTag("0");
        btd.add(serialNumber, ctd);
    }

    public void createPayRelaTradeData(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IData payRelaInfos = UcaInfoQry.qryLastPayRelaByUserId(userId);
        if (null == payRelaInfos)
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "复机：无法获取到用户的默认付费帐户！");
        }
        // 获取本账期的第一天
        String nowCycleStartStr = AcctDayDateUtil.getCycleIdFristDayThisAcct(userId);

        // 先终止一下还有效的账户付费关系
        IDataset validPayInfos = PayRelaInfoQry.getValidPayRelaByEndCycleId(userId, "1", "1", nowCycleStartStr);
        if (null != validPayInfos && validPayInfos.size() > 0)
        {
            for (int i = 0; i < validPayInfos.size(); i++)
            {
                PayRelationTradeData delTradeData = new PayRelationTradeData(validPayInfos.getData(i));
                delTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                // 失效时间为 本账期开始的前一天
                Date dd = SysDateMgr.string2Date(nowCycleStartStr, "yyyyMMdd");
                Date dtNew = DateUtils.addDays(dd, -1);
                delTradeData.setEndCycleId(SysDateMgr.date2String(dtNew, "yyyyMMdd"));
                btd.add(serialNumber, delTradeData);
            }
        }

        // 新增一个付费关系
        PayRelationTradeData payTradeData = new PayRelationTradeData(payRelaInfos);
        payTradeData.setStartCycleId(nowCycleStartStr);
        payTradeData.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payTradeData.setActTag("1");
        payTradeData.setInstId(SeqMgr.getInstId());
        payTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(serialNumber, payTradeData);

        // 修改付费账户的状态，如果检测到当前付费账户状态不正常了，则需要将状态修改为正常
        IData acctData = UcaInfoQry.qryAcctInfoByAcctId(payTradeData.getAcctId());
        if (null != acctData && !StringUtils.equals("0", acctData.getString("REMOVE_TAG", "")))// 非正常的账户则修改为正常
        {
            AccountTradeData acctTrade = new AccountTradeData(acctData);
            acctTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
            acctTrade.setRemoveTag("0");
            acctTrade.setRemoveDate("");
            btd.add(serialNumber, payTradeData);
        }
    }

    private void createProductTradeData(BusiTradeData btd) throws Exception
    {

        RestoreUserNpReqData reqData = (RestoreUserNpReqData) btd.getRD();
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(reqData.getUca().getUserId());
        productTD.setUserIdA("-1");

        productTD.setProductId(reqData.getMainProduct().getProductId());
        productTD.setProductMode(reqData.getMainProduct().getProductMode());
        productTD.setBrandCode(reqData.getMainProduct().getBrandCode());
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(reqData.getAcceptTime());

        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setMainTag("1");

        btd.add(serialNumber, productTD);
    }

    /**
     * 生成台帐资源子表参数
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public void createResTradeData(BusiTradeData btd) throws Exception
    {

        RestoreUserNpReqData reqData = (RestoreUserNpReqData) btd.getRD();
        List<ResInfo> resInfos = reqData.getResInfos();

        //
        if (resInfos != null)
        {
            for (ResInfo resInfo : resInfos)
            {
                ResTradeData restd = new ResTradeData();
                restd.setUserId(reqData.getUca().getUserId());
                restd.setUserIdA("-1");

                restd.setResCode(resInfo.getResCode());
                restd.setKi(resInfo.getKi());
                restd.setImsi(resInfo.getImsi());
                restd.setInstId(resInfo.getInstId());
                restd.setModifyTag(resInfo.getModifyTag());
                restd.setResTypeCode(resInfo.getResTypeCode());

                restd.setResTypeCode(resInfo.getResTypeCode());
                restd.setEndDate(SysDateMgr.END_DATE_FOREVER);
                restd.setStartDate(reqData.getAcceptTime());
                restd.setRsrvStr2(resInfo.getRsrvStr3());
                restd.setRsrvStr1(resInfo.getRsrvStr1());
                restd.setRsrvTag3(resInfo.getRsrvTag3());
                btd.add(reqData.getUca().getSerialNumber(), restd);
            }
        }

    }

    /**
     * @Function: createUserAndUserNpTradeData
     * @Description: 携入复机恢复用户携转资料
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     */
    public void createUserAndUserNpTradeData(BusiTradeData btd) throws Exception
    {

        RestoreUserNpReqData reqData = (RestoreUserNpReqData) btd.getRD();

        IDataset nps = UserNpInfoQry.qryUserNpInfosByUserId(reqData.getUca().getUserId());
        String npServiceType = "";
        String portInNetid = "";
        String portOutNetId = "";
        String portInDate = "";
        String bNpCardType = "";
        String aNpCardType = "";
        String homeNetid = "";
        if (IDataUtil.isNotEmpty(nps))
        {
            IData data = nps.getData(0);

            portInNetid = data.getString("PORT_IN_NETID");// 携入方网络ID
            portOutNetId = data.getString("PORT_OUT_NETID");// 携出方网络ID
            portInDate = data.getString("PORT_IN_DATE");
            npServiceType = data.getString("NP_SERVICE_TYPE");
            bNpCardType = data.getString("B_NP_CARD_TYPE");// 携入前用户类型
            aNpCardType = data.getString("A_NP_CARD_TYPE");// 携入后用户类型
            homeNetid = data.getString("HOME_NETID");
        }

        // IDataset ids = TradeNpQry.getValidTradeNpBySn(reqData.getUca().getUser().getSerialNumber());
        // String asp = "";
        // if(IDataUtil.isNotEmpty(ids)){
        // asp = ids.getData(0).getString("ASP");
        // }

        UserTradeData userTradeData = btd.getRD().getUca().getUser();

        RestoreUserNpBean bean = BeanManager.createBean(RestoreUserNpBean.class);

        UserTradeData newUserTradeData = userTradeData.clone();
        newUserTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        newUserTradeData.setRemoveTag("0");
        newUserTradeData.setUserTagSet(bean.getNewUserTagSet(reqData.getUca().getUserId()));
        newUserTradeData.setUserStateCodeset("0");
        newUserTradeData.setLastStopTime("");
        newUserTradeData.setDestroyTime("");
        newUserTradeData.setPreDestroyTime("");
        btd.add(userTradeData.getSerialNumber(), newUserTradeData);

        NpTradeData nptd = new NpTradeData();
        nptd.setUserId(reqData.getUca().getUserId());
        nptd.setTradeTypeCode("39");
        nptd.setNpServiceType(npServiceType);
        nptd.setSerialNumber(reqData.getUca().getSerialNumber());
        nptd.setFlowId("");
        nptd.setMessageId("");

        nptd.setBrcId("");
        nptd.setMsgCmdCode("");
        nptd.setMd5("");

        nptd.setPortOutNetid(portInNetid);
        nptd.setPortInNetid(portOutNetId);

        nptd.setHomeNetid(homeNetid);

        nptd.setANpCardType(aNpCardType); // 携入后用户类型
        nptd.setBNpCardType(bNpCardType);// 携入前用户类型
        nptd.setCustName(reqData.getUca().getCustPerson().getCustName());
        nptd.setPsptId(reqData.getUca().getCustPerson().getPsptId());
        nptd.setCredType(reqData.getUca().getCustPerson().getPsptTypeCode());
        nptd.setPhone(reqData.getUca().getCustPerson().getHomePhone());

        nptd.setNpStartDate(portInDate);// //NP启用时间
        nptd.setCreateTime(reqData.getAcceptTime());// 消息接受/生成时间
        nptd.setBookSendTime(reqData.getAcceptTime());// 消息预约发送时间

        nptd.setSendTimes("0");// 消息已经发送次数
        nptd.setResultCode("");// 结果代码
        nptd.setResultMessage(""); // 结果描述
        nptd.setErrorMessage("");// 拒绝消息
        nptd.setCancelTag("0");// 返销标志
        nptd.setState("000");// 状态编码 000-请求等待发送
        nptd.setRemark("携入复机");
        //新增受理员工信息 add by dengyi5
        nptd.setUpdateDepartId(CSBizBean.getVisit().getDepartId());
        nptd.setUpdateStaffId(CSBizBean.getVisit().getStaffId());
        nptd.setEparchyCode(CSBizBean.getTradeEparchyCode());
        btd.add(reqData.getUca().getSerialNumber(), nptd);

    }

    /**
     * @Function: CreateUserSvcStateTradeData
     * @Description: 用户服务状成台账 对应老完工流程CancleUserSvcState
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     */
    private void CreateUserSvcStateTradeData(BusiTradeData btd) throws Exception
    {

        RestoreUserNpReqData reqData = (RestoreUserNpReqData) btd.getRD();

        SvcStateTradeData svcStateTradeData = new SvcStateTradeData();
        svcStateTradeData.setUserId(reqData.getUca().getUserId());
        svcStateTradeData.setServiceId("0");// 语音
        svcStateTradeData.setMainTag("1");
        svcStateTradeData.setStateCode("0");
        svcStateTradeData.setStartDate(reqData.getAcceptTime());
        svcStateTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        svcStateTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        svcStateTradeData.setInstId(SeqMgr.getInstId());
        btd.add(reqData.getUca().getSerialNumber(), svcStateTradeData);
    }

    // 一定要放在服务状态台账生成之后处理
    private void modifyUserMainSvcStateGroups(BusiTradeData btd) throws Exception
    {
        ChangeSvcStateComm bean = new ChangeSvcStateComm();
        bean.modifyMainSvcStateByUserId(btd);
        // 放在最后面，不要影响到主体服务状态
        if (StringUtils.equals("310", btd.getTradeTypeCode()))// 如果是立即复机业务的话，还需要清理一下扩展字段的标记
        {
            bean.clearRsrvSpecTagInfo(btd);
        }
    }

}
