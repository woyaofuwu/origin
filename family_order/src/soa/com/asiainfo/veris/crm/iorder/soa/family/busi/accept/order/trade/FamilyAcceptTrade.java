
package com.asiainfo.veris.crm.iorder.soa.family.busi.accept.order.trade;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.accept.order.requestdata.FamilyAcceptReqData;
import com.asiainfo.veris.crm.iorder.soa.family.common.trade.BaseFamilyBusiTrade;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.*;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;

/**
 * @Description 家庭资料登记类
 * @Auther: zhenggang
 * @Date: 2020/7/30 10:25
 * @version: V1.0
 */
public class FamilyAcceptTrade extends BaseFamilyBusiTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        FamilyAcceptReqData reqData = (FamilyAcceptReqData) btd.getRD();
        String userId = reqData.getUca().getUserId();
        String acctId = reqData.getUca().getAcctId();
        btd.getMainTradeData().setRsrvStr1(reqData.getFmyProductId());
        btd.getMainTradeData().setRsrvStr2(reqData.getManagerSn());// 管理员号码
        btd.getMainTradeData().setRsrvStr3(reqData.getIsEffectNow());
        btd.getMainTradeData().setUserId(userId);
        btd.getMainTradeData().setAcctId(acctId);
        // 创建家庭用户资料
        createUserTradeData(btd);
        // 创建家庭客户资料
        createCustFamilyTradeData(btd);
        // 创建家庭产品资料
        createProductTradeData(btd, userId);
        // 创建家庭服务资料
        createSvcTrade(btd, userId);
        // 创建家庭账户资料
        createAccountTrade(btd);
        // 创建用户账期资料
        btd.addOpenUserAcctDayData(reqData.getUca().getUserId(), reqData.getUca().getAcctDay());
        // 创建账户账期资料
        btd.addOpenAccountAcctDayData(reqData.getUca().getAcctId(), reqData.getUca().getAcctDay());
        // 创建家庭付费关系资料
        createPayRelationTrade(btd, userId);
        // 创建家庭资源资料
        createResTradeData(btd, userId);
        // 创建家庭计费资料
        createScoreAcctData(btd);
        // 创建家庭商品资料
        super.createBusiTradeData(btd);
    }

    private void createUserTradeData(BusiTradeData btd) throws Exception
    {
        UserTradeData utd = btd.getRD().getUca().getUser();
        utd.setRsrvStr1(btd.getRD().getOrderId());
        btd.add(btd.getRD().getUca().getSerialNumber(), utd);
    }

    private void createCustFamilyTradeData(BusiTradeData btd) throws Exception
    {
        CustFamilyTradeData utd = btd.getRD().getUca().getCustFamily();
        utd.setRsrvStr1(btd.getRD().getOrderId());
        btd.add(btd.getRD().getUca().getSerialNumber(), utd);
    }

    private void createAccountTrade(BusiTradeData btd) throws Exception
    {
        AccountTradeData accountTradeData = btd.getRD().getUca().getAccount();
        btd.add(btd.getRD().getUca().getSerialNumber(), accountTradeData);
    }

    private void createProductTradeData(BusiTradeData btd, String userId) throws Exception
    {
        FamilyAcceptReqData reqData = (FamilyAcceptReqData) btd.getRD();
        ProductTradeData ptd = new ProductTradeData();
        ptd.setUserId(userId);
        ptd.setUserIdA("-1");
        ptd.setInstId(SeqMgr.getInstId());
        ptd.setProductId(reqData.getFmyProductId());
        ptd.setBrandCode(reqData.getFmyBrandCode());
        ptd.setProductMode(reqData.getFmyProductMode());
        ptd.setMainTag("1");
        ptd.setStartDate(btd.getRD().getAcceptTime());
        ptd.setEndDate(SysDateMgr.getTheLastTime());
        ptd.setRemark("家庭用户主产品");
        ptd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        ptd.setRsrvStr1("FAMILY_PRODUCT_ID"); // 虚拟用户主产品标记
        btd.add(btd.getRD().getUca().getSerialNumber(), ptd);
    }

    private void createSvcTrade(BusiTradeData btd, String userId) throws Exception
    {
        SvcTradeData svcTD = new SvcTradeData();
        svcTD.setUserId(userId);
        svcTD.setUserIdA("-1");
        svcTD.setProductId("100000");
        svcTD.setPackageId("-1");
        svcTD.setElementId("1999");
        svcTD.setMainTag("1");
        svcTD.setInstId(SeqMgr.getInstId());
        svcTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        svcTD.setStartDate(btd.getRD().getAcceptTime());
        svcTD.setEndDate(SysDateMgr.getTheLastTime());
        svcTD.setRemark("家庭用户主服务");
        // TODO 先注释，海南没有这个虚拟服务
        // btd.add(btd.getRD().getUca().getSerialNumber(), svcTD);
    }

    private void createPayRelationTrade(BusiTradeData btd, String userId) throws Exception
    {
        FamilyAcceptReqData reqData = (FamilyAcceptReqData) btd.getRD();
        IData mainUser = UcaInfoQry.qryUserInfoBySn(reqData.getManagerSn());
        if (IDataUtil.isEmpty(mainUser))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取管理员用户信息无数据");
        }
        IData account = UcaInfoQry.qryAcctInfoByUserId(mainUser.getString("USER_ID"));
        if (IDataUtil.isEmpty(account))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取管理员账户信息无数据");
        }
        PayRelationTradeData payRelationTradeData = new PayRelationTradeData();
        String acctId = btd.getRD().getUca().getAcctId();
        payRelationTradeData.setUserId(userId);
        payRelationTradeData.setAcctId(acctId);
        payRelationTradeData.setPayitemCode("-1");
        payRelationTradeData.setAcctPriority("0");
        payRelationTradeData.setUserPriority("0");
        payRelationTradeData.setBindType("1");
        payRelationTradeData.setStartCycleId(SysDateMgr.getNowCycle());
        payRelationTradeData.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payRelationTradeData.setActTag("1");
        payRelationTradeData.setDefaultTag("0"); // 暂时设置为0，完工时改成1
        payRelationTradeData.setLimitType("0");
        payRelationTradeData.setLimit("0");
        payRelationTradeData.setComplementTag("0");
        payRelationTradeData.setRemark("家庭用户付费关系");
        payRelationTradeData.setInstId(SeqMgr.getInstId());
        payRelationTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        payRelationTradeData.setRsrvStr1("V"); // 虚拟标记
        btd.add(btd.getRD().getUca().getSerialNumber(), payRelationTradeData);
    }

    private void createResTradeData(BusiTradeData btd, String userId) throws Exception
    {
        String instId = SeqMgr.getInstId();
        ResTradeData resTradeDataNew = new ResTradeData();
        resTradeDataNew.setUserId(userId);
        resTradeDataNew.setUserIdA("-1");
        resTradeDataNew.setResTypeCode("0");
        resTradeDataNew.setResCode(btd.getRD().getUca().getSerialNumber());
        resTradeDataNew.setImsi("0");
        resTradeDataNew.setInstId(instId);
        resTradeDataNew.setStartDate(btd.getRD().getAcceptTime());
        resTradeDataNew.setEndDate(SysDateMgr.END_DATE_FOREVER);
        resTradeDataNew.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resTradeDataNew.setRemark("家庭用户");
        btd.add(btd.getRD().getUca().getSerialNumber(), resTradeDataNew);
    }

    private void createScoreAcctData(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        // 插TF_B_TRADE_INTEGRALACCT TF_B_TRADE_SCORERELATION 和 TF_B_TRADE_INTEGRALPLAN
        String integralAcctId = SeqMgr.getAcctId();
        IntegralAcctTradeData integralAcctTradeData = new IntegralAcctTradeData();
        integralAcctTradeData.setIntegralAcctId(integralAcctId);
        integralAcctTradeData.setIntegralAccountType("0");
        integralAcctTradeData.setName(uca.getCustomer().getCustName());
        integralAcctTradeData.setUserId(uca.getUserId());
        integralAcctTradeData.setAcctId(uca.getAcctId());
        integralAcctTradeData.setCustId(uca.getCustId());
        integralAcctTradeData.setContractPhone(uca.getSerialNumber());
        integralAcctTradeData.setPsptTypeCode(uca.getCustomer().getPsptTypeCode());
        integralAcctTradeData.setPsptId(uca.getCustomer().getPsptId());
        integralAcctTradeData.setAddress(uca.getCustPerson().getHomeAddress());
        integralAcctTradeData.setEmail(uca.getCustPerson().getEmail());
        integralAcctTradeData.setPassword(uca.getUser().getUserPasswd());
        integralAcctTradeData.setUseLimit("");
        integralAcctTradeData.setStartDate(btd.getRD().getAcceptTime());
        integralAcctTradeData.setEndDate(SysDateMgr.getTheLastTime());
        integralAcctTradeData.setStatus("10A");
        integralAcctTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(uca.getSerialNumber(), integralAcctTradeData);
        // TF_B_TRADE_SCORERELATION
        ScoreRelationTradeData scoreRelationTradeData = new ScoreRelationTradeData();
        scoreRelationTradeData.setPayrelationId(SeqMgr.getAcctId());
        scoreRelationTradeData.setUserId(uca.getUserId());
        scoreRelationTradeData.setLimitType("1");
        scoreRelationTradeData.setLimitValue("0");
        scoreRelationTradeData.setIntegralAcctId(integralAcctId);
        scoreRelationTradeData.setDefaultTag("1");
        scoreRelationTradeData.setActTag("1");// 1生效 0失效
        scoreRelationTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        scoreRelationTradeData.setStartDate(SysDateMgr.decodeTimestamp(btd.getRD().getAcceptTime(), "yyyyMMdd"));
        scoreRelationTradeData.setEndDate(SysDateMgr.decodeTimestamp(SysDateMgr.getTheLastTime(), "yyyyMMdd"));
        btd.add(uca.getSerialNumber(), scoreRelationTradeData);
    }
}
