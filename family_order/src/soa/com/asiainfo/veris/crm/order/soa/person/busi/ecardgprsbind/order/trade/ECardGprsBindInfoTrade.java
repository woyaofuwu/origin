
package com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbind.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbind.order.EcardGprsBindUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbind.order.requestdata.ECardGprsBindReqData;

/**
 * 随E行绑定TRADE类
 */
public class ECardGprsBindInfoTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        String userIdA = getUserIdA(btd);
        // 分散账期新增 add by
        // acctDayDeal(btd);

        // 新增结束

        if (StringUtils.isBlank(userIdA))
        {
            userIdA = SeqMgr.getUserId();
            geneTradeRelation(btd, userIdA);
            geneTradeDiscnt(btd, userIdA);
            // 新增虚拟用户账期信息在新增用户信息的时候统一完成
            geneTradeUser(btd, userIdA);
        }
        geneTableTradeMain(btd);
        geneTradeRelationEcard(btd, userIdA);
        geneTradePayrelation(btd, userIdA);
        geneTradeOtherEcard(btd);

    }

    private void geneTableTradeMain(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        ECardGprsBindReqData reqData = (ECardGprsBindReqData) btd.getRD();
        UcaData ucaData = reqData.getUca(); // 主卡UCA信息
        if (!StringUtils.equals("1", ucaData.getAcctDay()))
        {
            btd.addChangeAcctDayData(ucaData.getUserId(), "1");
        }
        UcaData ecardUcaData = UcaDataFactory.getNormalUcaByQry(reqData.getE_serial_number(), false, false);
        if (!StringUtils.equals("1", ecardUcaData.getAcctDay()))
        {
            btd.addChangeAcctDayData(reqData.getE_user_id(), "1");
        }
        btd.getMainTradeData().setRsrvStr5(reqData.getE_serial_number());// 随E行号码
        btd.getMainTradeData().setRsrvStr6(reqData.getE_user_id());// 随E行userId
    }

    private void geneTradeDiscnt(BusiTradeData btd, String userIdA) throws Exception
    {
        // TODO Auto-generated method stub
        ECardGprsBindReqData reqData = (ECardGprsBindReqData) btd.getRD();
        UcaData ucaDta = reqData.getUca();

        DiscntTradeData discntTradeData = new DiscntTradeData();
        discntTradeData.setUserId(userIdA);
        discntTradeData.setUserIdA("-1");
        discntTradeData.setPackageId("-1");
        discntTradeData.setProductId("-1");
        discntTradeData.setElementId("5904");
        discntTradeData.setStartDate(reqData.getAcceptTime());
        discntTradeData.setEndDate(SysDateMgr.getTheLastTime());
        discntTradeData.setSpecTag("0");
        discntTradeData.setRelationTypeCode("80");
        discntTradeData.setInstId(SeqMgr.getInstId());
        discntTradeData.setCampnId("0");
        discntTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

        btd.add(ucaDta.getSerialNumber(), discntTradeData);
    }

    private void geneTradeOtherEcard(BusiTradeData btd) throws Exception
    {
        ECardGprsBindReqData rd = (ECardGprsBindReqData) btd.getRD();
        UcaData ucaDta = rd.getUca();

        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setUserId(rd.getE_user_id());
        otherTradeData.setRsrvValueCode("EACT");
        IData oldPayRalationEcard = UcaInfoQry.qryDefaultPayRelaByUserId(rd.getE_user_id());
        String oldAcctId = "";
        if (IDataUtil.isNotEmpty(oldPayRalationEcard))
        {
            oldAcctId = oldPayRalationEcard.getString("ACCT_ID");
        }
        otherTradeData.setRsrvValue(oldAcctId);
        otherTradeData.setStartDate(rd.getAcceptTime());
        otherTradeData.setEndDate(SysDateMgr.END_TIME_FOREVER);
    }

    private void geneTradePayrelation(BusiTradeData btd, String userIdA) throws Exception
    {
        // TODO Auto-generated method stub

        ECardGprsBindReqData reqData = (ECardGprsBindReqData) btd.getRD();
        UcaData ucaDta = reqData.getUca();
        PayRelationTradeData payRelationTradeData = new PayRelationTradeData();

        payRelationTradeData.setUserId(reqData.getE_user_id());
        payRelationTradeData.setAcctId(ucaDta.getAcctId());
        payRelationTradeData.setPayitemCode("40000");
        payRelationTradeData.setAcctPriority("0");
        payRelationTradeData.setUserPriority("0");
        payRelationTradeData.setBindType("0");
        payRelationTradeData.setActTag("0");
        payRelationTradeData.setDefaultTag("0");
        payRelationTradeData.setLimitType("0");
        payRelationTradeData.setLimit("0");
        payRelationTradeData.setComplementTag("0");
        payRelationTradeData.setInstId(SeqMgr.getInstId());
        payRelationTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

        EcardGprsBindUtil ecardGprsBU = new EcardGprsBindUtil();
        IData newSninfoData = ecardGprsBU.checkAfterNewSnInfo(btd, userIdA);

        payRelationTradeData.setStartCycleId(newSninfoData.getString("startDate"));
        payRelationTradeData.setEndCycleId(SysDateMgr.getEndCycle20501231());

        btd.add(ucaDta.getSerialNumber(), payRelationTradeData);
    }

    private void geneTradeRelation(BusiTradeData btd, String userIdA) throws Exception
    {
        ECardGprsBindReqData reqData = (ECardGprsBindReqData) btd.getRD();
        UcaData ucaDta = reqData.getUca();

        RelationTradeData relationTradeData = new RelationTradeData();

        String serialNumberA = "80" + ucaDta.getSerialNumber();
        relationTradeData.setUserIdA(userIdA);
        relationTradeData.setSerialNumberA(serialNumberA);
        relationTradeData.setUserIdB(ucaDta.getUserId());
        relationTradeData.setSerialNumberB(ucaDta.getSerialNumber());
        relationTradeData.setRelationTypeCode("80");
        relationTradeData.setRoleTypeCode("0");
        relationTradeData.setRoleCodeA("0");
        relationTradeData.setRoleCodeB("1");
        relationTradeData.setOrderno("0");
        relationTradeData.setInstId(SeqMgr.getInstId());
        relationTradeData.setStartDate(reqData.getAcceptTime());
        relationTradeData.setEndDate(SysDateMgr.getTheLastTime());
        relationTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

        btd.add(ucaDta.getSerialNumber(), relationTradeData);
    }

    private void geneTradeRelationEcard(BusiTradeData btd, String userIdA) throws Exception
    {
        // TODO Auto-generated method stub
        ECardGprsBindReqData reqData = (ECardGprsBindReqData) btd.getRD();
        UcaData ucaDta = reqData.getUca();
        RelationTradeData relationTradeData = new RelationTradeData();

        relationTradeData.setUserIdA(userIdA);
        relationTradeData.setSerialNumberA("80" + ucaDta.getSerialNumber());
        relationTradeData.setUserIdB(reqData.getE_user_id());
        relationTradeData.setSerialNumberB(reqData.getE_serial_number());
        relationTradeData.setRelationTypeCode("80");
        relationTradeData.setRoleTypeCode("0");
        relationTradeData.setRoleCodeA("0");
        relationTradeData.setRoleCodeB("2");
        relationTradeData.setOrderno("1");
        relationTradeData.setInstId(SeqMgr.getInstId());
        relationTradeData.setStartDate(reqData.getAcceptTime());
        relationTradeData.setEndDate(SysDateMgr.getTheLastTime());
        relationTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

        btd.add(ucaDta.getSerialNumber(), relationTradeData);
    }

    private void geneTradeUser(BusiTradeData btd, String userIdA) throws Exception
    {

        ECardGprsBindReqData reqData = (ECardGprsBindReqData) btd.getRD();
        UcaData ucaDta = reqData.getUca();

        CustomerTradeData custData = ucaDta.getCustomer();
        UserTradeData userData = new UserTradeData();// ucaDta.getUser().clone();

        String serialNumber = "80" + ucaDta.getSerialNumber(); // User表的SerialNumber
        String custId = custData.getCustId();

        userData.setUserId(userIdA);
        userData.setSerialNumber(serialNumber);
        userData.setUserTypeCode("0");
        userData.setUserStateCodeset("0");
        userData.setAcctTag("0");
        userData.setOpenMode("0");
        userData.setCustId(custId);
        userData.setUsecustId(custId);
        userData.setMputeMonthFee("0");
        userData.setNetTypeCode(ucaDta.getUser().getNetTypeCode());
        userData.setEparchyCode(ucaDta.getUser().getEparchyCode());
        userData.setCityCode(ucaDta.getUser().getCityCode());
        userData.setInDate(reqData.getAcceptTime());
        userData.setInStaffId(CSBizBean.getVisit().getStaffId());// 建档员工
        userData.setInDepartId(CSBizBean.getVisit().getDepartId());// 建档渠道
        userData.setOpenDate(reqData.getAcceptTime());// 开户时间
        userData.setOpenStaffId(CSBizBean.getVisit().getStaffId());// 开户员工
        userData.setOpenDepartId(CSBizBean.getVisit().getDepartId());// 开户渠道
        userData.setPrepayTag("1");// 预付费标记：0：后付费，1：预付费
        userData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        userData.setRemoveTag("0");

        btd.add(ucaDta.getSerialNumber(), userData);

        btd.addOpenUserAcctDayData(userIdA, "1");
    }

    private String getUserIdA(BusiTradeData btd) throws Exception
    {
        ECardGprsBindReqData rd = (ECardGprsBindReqData) btd.getRD();
        UserTradeData userInfo = rd.getUca().getUser();
        // 获取绑定信息
        IDataset relationInfos = null;
        relationInfos = RelaUUInfoQry.getRelationUUInfoByDeputySn(userInfo.getUserId(), "80", null);
        String userIdA = "";
        if (relationInfos.size() > 0)
        {
            IData relationInfo = relationInfos.getData(0);
            userIdA = relationInfo.getString("USER_ID_A", "");
        }
        return userIdA;
    }

}
